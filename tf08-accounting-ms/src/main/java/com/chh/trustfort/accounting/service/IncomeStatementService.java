package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.Responses.ReportViewerResponse;
import com.chh.trustfort.accounting.dto.IncomeStatementResponse;
import com.chh.trustfort.accounting.dto.ReportLineItem;
import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.enums.TransactionType;
import com.chh.trustfort.accounting.model.JournalEntry;
import com.chh.trustfort.accounting.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class IncomeStatementService {

    private final JournalEntryRepository journalEntryRepository;

    public IncomeStatementResponse generateIncomeStatement(StatementFilterDTO filter) {
        List<JournalEntry> entries = journalEntryRepository.findByStatementFilters(filter);

        // Apply filters
        if (filter.getDepartment() != null && !filter.getDepartment().isBlank()) {
            entries = entries.stream()
                    .filter(e -> filter.getDepartment().equalsIgnoreCase(e.getDepartment()))
                    .collect(Collectors.toList());
        }

        if (filter.getBusinessUnit() != null && !filter.getBusinessUnit().isBlank()) {
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

        // Totals
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        // Line items
        List<ReportLineItem> revenueItems = new ArrayList<>();
        List<ReportLineItem> expenseItems = new ArrayList<>();

        for (JournalEntry entry : entries) {
            AccountClassification classification = entry.getAccount().getClassification();
            if (classification == null) {
                log.warn("🚨 Account classification missing for account: {}", entry.getAccount().getAccountName());
                continue;
            }
            BigDecimal amount = entry.getTransactionType() == TransactionType.CREDIT
                    ? entry.getAmount()
                    : entry.getAmount().negate(); // DEBIT values are negative

            if (classification == AccountClassification.REVENUE) {
                BigDecimal normalized = amount.abs(); // Show positive revenue
                totalRevenue = totalRevenue.add(normalized);
                revenueItems.add(ReportLineItem.builder()
                        .label(entry.getAccount().getAccountName())
                        .amount(normalized)
                        .account(entry.getAccount())
                        .build());
            }

            if (classification == AccountClassification.EXPENSE) {
                BigDecimal normalized = amount.abs(); // Show positive expense
                totalExpenses = totalExpenses.add(normalized);
                expenseItems.add(ReportLineItem.builder()
                        .label(entry.getAccount().getAccountName())
                        .amount(normalized)
                        .account(entry.getAccount())
                        .build());
            }
        }

        BigDecimal netIncome = totalRevenue.subtract(totalExpenses);

        return IncomeStatementResponse.builder()
                .totalRevenue(totalRevenue)
                .totalExpenses(totalExpenses)
                .netIncome(netIncome)
                .revenueItems(revenueItems)
                .expenseItems(expenseItems)
                .build();
    }


    public List<ReportViewerResponse> generateIncomeStatementForViewer(StatementFilterDTO filter) {
        IncomeStatementResponse response = generateIncomeStatement(filter);
        List<ReportViewerResponse> viewerResponses = new ArrayList<>();

        if (response.getRevenueItems() != null) {
            for (ReportLineItem item : response.getRevenueItems()) {
                Map<String, Object> fields = new LinkedHashMap<>();
                fields.put("section", "REVENUE");
                fields.put("groupName", item.getAccount().getClassification().name());
                fields.put("accountCode", item.getAccount().getAccountCode());
                fields.put("accountName", item.getAccount().getAccountName());
                fields.put("amount", item.getAmount());
                viewerResponses.add(new ReportViewerResponse(fields));
            }
        }

        if (response.getExpenseItems() != null) {
            for (ReportLineItem item : response.getExpenseItems()) {
                Map<String, Object> fields = new LinkedHashMap<>();
                fields.put("section", "EXPENSE");
                fields.put("groupName", item.getAccount().getClassification().name());
                fields.put("accountCode", item.getAccount().getAccountCode());
                fields.put("accountName", item.getAccount().getAccountName());
                fields.put("amount", item.getAmount());
                viewerResponses.add(new ReportViewerResponse(fields));
            }
        }

        // Add final summary
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("section", "SUMMARY");
        summary.put("groupName", "");
        summary.put("accountCode", "");
        summary.put("accountName", "Net Income");
        summary.put("amount", response.getNetIncome());
        viewerResponses.add(new ReportViewerResponse(summary));

        return viewerResponses;
    }

}


