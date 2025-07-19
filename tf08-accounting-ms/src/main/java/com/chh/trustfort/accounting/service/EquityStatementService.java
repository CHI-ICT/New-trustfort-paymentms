package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.Responses.ReportViewerResponse;
import com.chh.trustfort.accounting.dto.EquityStatementResponse;
import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.enums.TransactionType;
import com.chh.trustfort.accounting.model.JournalEntry;
import com.chh.trustfort.accounting.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;


import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquityStatementService {

    private final JournalEntryRepository journalEntryRepository;

    public EquityStatementResponse generateStatement(StatementFilterDTO filter) {
        // Step 1: Fetch equity entries within the reporting period
        List<JournalEntry> entries = journalEntryRepository.findEquityEntriesBetweenDates(
                filter.getStartDate(), filter.getEndDate(), AccountClassification.EQUITY);

        // Step 2: Optional filters for department and business unit
        if (filter.getDepartment() != null && !filter.getDepartment().isBlank()) {
            entries = entries.stream()
                    .filter(entry -> filter.getDepartment().equalsIgnoreCase(entry.getDepartment()))
                    .collect(Collectors.toList());
        }

        if (filter.getBusinessUnit() != null && !filter.getBusinessUnit().isBlank()) {
            entries = entries.stream()
                    .filter(entry -> filter.getBusinessUnit().equalsIgnoreCase(entry.getBusinessUnit()))
                    .collect(Collectors.toList());
        }

        // Step 3: Categorize entries into components
        BigDecimal retainedEarnings = BigDecimal.ZERO;
        BigDecimal contributions = BigDecimal.ZERO;
        BigDecimal dividends = BigDecimal.ZERO;

        for (JournalEntry entry : entries) {
            String name = entry.getAccount().getAccountName().toLowerCase();
            BigDecimal amount = entry.getTransactionType() == TransactionType.DEBIT
                    ? entry.getAmount().negate() : entry.getAmount();

            if (name.contains("retained")) {
                retainedEarnings = retainedEarnings.add(amount);
            } else if (name.contains("contribution") || name.contains("capital")) {
                contributions = contributions.add(amount);
            } else if (name.contains("dividend")) {
                dividends = dividends.add(amount);
            }
        }

        // Step 4: Compute Opening Equity from entries before reporting period
        BigDecimal openingEquity = journalEntryRepository
                .findEquityEntriesBeforeDate(filter.getStartDate(), AccountClassification.EQUITY).stream()
                .map(e -> e.getTransactionType() == TransactionType.DEBIT ? e.getAmount().negate() : e.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Step 5: Compute closing equity
        BigDecimal closingEquity = openingEquity
                .add(contributions)
                .add(retainedEarnings)
                .subtract(dividends);

        return EquityStatementResponse.builder()
                .openingEquity(openingEquity)
                .contributions(contributions)
                .retainedEarnings(retainedEarnings)
                .dividends(dividends)
                .closingEquity(closingEquity)
                .build();
    }

    public List<ReportViewerResponse> generateEquityStatementForViewer(StatementFilterDTO filter) {
        EquityStatementResponse response = generateStatement(filter);

        List<ReportViewerResponse> rows = new ArrayList<>();
        rows.add(new ReportViewerResponse(Map.of("Description", "Opening Equity", "Amount", response.getOpeningEquity())));
        rows.add(new ReportViewerResponse(Map.of("Description", "Contributions during the year", "Amount", response.getContributions())));
        rows.add(new ReportViewerResponse(Map.of("Description", "Retained Earnings", "Amount", response.getRetainedEarnings())));
        rows.add(new ReportViewerResponse(Map.of("Description", "Dividends Paid", "Amount", response.getDividends().negate()))); // Display as negative
        rows.add(new ReportViewerResponse(Map.of("Description", "Closing Equity", "Amount", response.getClosingEquity())));

        return rows;
    }
}
