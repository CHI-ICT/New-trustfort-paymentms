package com.chh.trustfort.accounting.repository.customImpl;

import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.enums.TransactionType;
import com.chh.trustfort.accounting.model.JournalEntry;
import com.chh.trustfort.accounting.repository.custom.JournalEntryRepositoryCustom;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JournalEntryRepositoryImpl implements JournalEntryRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<JournalEntry> findByStatementFilters(StatementFilterDTO filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<JournalEntry> query = cb.createQuery(JournalEntry.class);
        Root<JournalEntry> root = query.from(JournalEntry.class);
        List<Predicate> predicates = new ArrayList<>();

        // Join with account
        Join<Object, Object> accountJoin = root.join("account");

        // Filter: date range
        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            predicates.add(cb.between(
                    root.get("transactionDate"),
                    filter.getStartDate().atStartOfDay(),
                    filter.getEndDate().atTime(23, 59)
            ));
        }

        // Filter: classification (REVENUE and EXPENSE only)
        predicates.add(accountJoin.get("classification")
                .in(AccountClassification.REVENUE, AccountClassification.EXPENSE));

        // Filter: department
        if (filter.getDepartment() != null) {
            predicates.add(cb.equal(cb.lower(root.get("department")),
                    filter.getDepartment().toLowerCase()));
        }

        // Filter: business unit
        if (filter.getBusinessUnit() != null) {
            predicates.add(cb.equal(cb.lower(root.get("businessUnit")),
                    filter.getBusinessUnit().toLowerCase()));
        }

        // Filter: transaction type
        if (filter.getTransactionType() != null) {
            predicates.add(cb.equal(root.get("transactionType"), filter.getTransactionType()));
        }

        // Filter: min amount
        if (filter.getMinAmount() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), filter.getMinAmount()));
        }

        // Filter: max amount
        if (filter.getMaxAmount() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("amount"), filter.getMaxAmount()));
        }

        // Apply all predicates
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getResultList();
    }
}
