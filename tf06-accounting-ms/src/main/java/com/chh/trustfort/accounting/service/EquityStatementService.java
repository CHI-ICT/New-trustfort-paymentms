package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.EquityStatementResponse;
import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.enums.TransactionType;
import com.chh.trustfort.accounting.model.JournalEntry;
import com.chh.trustfort.accounting.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquityStatementService {

    private final JournalEntryRepository journalEntryRepository;

    public EquityStatementResponse generateStatement(StatementFilterDTO filter) {

        List<JournalEntry> entries = journalEntryRepository.findEquityEntriesBetweenDates(
                filter.getStartDate(), filter.getEndDate(), AccountClassification.EQUITY);

        // Filter by department and business unit
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

        // Initialize components
        BigDecimal retainedEarnings = BigDecimal.ZERO;
        BigDecimal contributions = BigDecimal.ZERO;
        BigDecimal dividends = BigDecimal.ZERO;

        for (JournalEntry entry : entries) {
            String name = entry.getAccount().getName().toLowerCase();
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

        // Stubbed opening equity - replace with logic if historical tracking is required
        BigDecimal openingEquity = BigDecimal.ZERO;

        // Closing equity = opening + contributions + retained earnings - dividends
        BigDecimal closingEquity = openingEquity
                .add(contributions)
                .add(retainedEarnings)
                .subtract(dividends);

        // Build response
        EquityStatementResponse response = new EquityStatementResponse();
        response.setOpeningEquity(openingEquity);
        response.setContributions(contributions);
        response.setRetainedEarnings(retainedEarnings);
        response.setDividends(dividends);
        response.setClosingEquity(closingEquity);

        return response;
    }
}

