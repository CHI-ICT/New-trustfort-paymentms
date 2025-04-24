package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.IncomeStatementResponse;
import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.enums.TransactionType;
import com.chh.trustfort.accounting.model.JournalEntry;
import com.chh.trustfort.accounting.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncomeStatementService {

    private final JournalEntryRepository journalEntryRepository;

    public IncomeStatementResponse generateIncomeStatement(StatementFilterDTO filter) {
        LocalDate start = filter.getStartDate();
        LocalDate end = filter.getEndDate();

        // Step 1: Fetch revenue and expense entries from DB
        List<JournalEntry> entries = journalEntryRepository.findByStatementFilters(filter);

        // Step 2: Apply additional filters
        if (filter.getDepartment() != null) {
            entries = entries.stream()
                    .filter(e -> filter.getDepartment().equalsIgnoreCase(e.getDepartment()))
                    .collect(Collectors.toList());
        }

        if (filter.getBusinessUnit() != null) {
            entries = entries.stream()
                    .filter(e -> filter.getBusinessUnit().equalsIgnoreCase(e.getBusinessUnit()))
                    .collect(Collectors.toList());
        }

        if (filter.getMinAmount() != null) {
            entries = entries.stream()
                    .filter(e -> e.getAmount().compareTo(filter.getMinAmount()) >= 0)
                    .collect(Collectors.toList());
        }

        if (filter.getMaxAmount() != null) {
            entries = entries.stream()
                    .filter(e -> e.getAmount().compareTo(filter.getMaxAmount()) <= 0)
                    .collect(Collectors.toList());
        }

        if (filter.getTransactionType() != null) {
            entries = entries.stream()
                    .filter(e -> e.getTransactionType() == filter.getTransactionType())
                    .collect(Collectors.toList());
        }

        // Step 3: Aggregate totals
        BigDecimal totalRevenue = entries.stream()
                .filter(e -> e.getAccount().getClassification() == AccountClassification.REVENUE)
                .map(e -> e.getTransactionType() == TransactionType.DEBIT ? e.getAmount().negate() : e.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = entries.stream()
                .filter(e -> e.getAccount().getClassification() == AccountClassification.EXPENSE)
                .map(e -> e.getTransactionType() == TransactionType.DEBIT ? e.getAmount() : e.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netIncome = totalRevenue.subtract(totalExpenses);

        // Step 4: Build response
        IncomeStatementResponse response = new IncomeStatementResponse();
        response.setTotalRevenue(totalRevenue);
        response.setTotalExpenses(totalExpenses);
        response.setNetIncome(netIncome);

        return response;
    }
}


