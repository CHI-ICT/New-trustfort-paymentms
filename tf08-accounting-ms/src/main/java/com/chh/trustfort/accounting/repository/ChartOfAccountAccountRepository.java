package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.enums.ExpenseType;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChartOfAccountAccountRepository extends JpaRepository<ChartOfAccount, Long> {
    boolean existsByAccountCode(String accoundCode);

    Optional<ChartOfAccount> findByAccountCode(String accountCode);

//    Optional<ChartOfAccount> findByExpenseType(ExpenseType expenseType);

    @Query("SELECT a FROM ChartOfAccount a " +
            "WHERE a.classification = 'ASSET' AND LOWER(a.accountName) LIKE %:keyword% AND a.status = 'ACTIVE'")
    List<ChartOfAccount> findCashOrBankAccounts(@Param("keyword") String keyword);

    List<ChartOfAccount> findAllByExpenseType(ExpenseType expenseType);
}
