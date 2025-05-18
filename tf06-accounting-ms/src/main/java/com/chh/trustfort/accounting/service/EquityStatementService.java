package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.EquityStatementResponse;
import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.enums.TransactionType;
import com.chh.trustfort.accounting.model.JournalEntry;
import com.chh.trustfort.accounting.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquityStatementService {

    private final JournalEntryRepository journalEntryRepository;

    public EquityStatementResponse generateStatement(StatementFilterDTO filter) {
        List<JournalEntry> entries = journalEntryRepository.findEquityEntriesBetweenDates(
                filter.getStartDate().atStartOfDay(),
                filter.getEndDate().atTime(23, 59),
                AccountClassification.EQUITY
        );

        if (filter.getDepartment() != null) {
            entries = entries.stream()
                    .filter(entry -> filter.getDepartment().equalsIgnoreCase(entry.getDepartment()))
                    .collect(Collectors.toList()); // ✅ Java 8+ compatible
        }

        if (filter.getBusinessUnit() != null) {
            entries = entries.stream()
                    .filter(entry -> filter.getBusinessUnit().equalsIgnoreCase(entry.getBusinessUnit()))
                    .collect(Collectors.toList()); // ✅ Java 8+ compatible
        }

        BigDecimal retainedEarnings = BigDecimal.ZERO;
        BigDecimal contributions = BigDecimal.ZERO;
        BigDecimal dividends = BigDecimal.ZERO;

        for (JournalEntry entry : entries) {
            String accountName = entry.getAccount().getName().toLowerCase();
            BigDecimal amount = entry.getTransactionType() == TransactionType.DEBIT
                    ? entry.getAmount().negate()
                    : entry.getAmount();

            if (accountName.contains("retained")) {
                retainedEarnings = retainedEarnings.add(amount);
            } else if (accountName.contains("contribution")) {
                contributions = contributions.add(amount);
            } else if (accountName.contains("dividend")) {
                dividends = dividends.add(amount);
            }
        }

        BigDecimal openingEquity = BigDecimal.ZERO; // can be extended from previous period
        BigDecimal closingEquity = openingEquity
                .add(contributions)
                .add(retainedEarnings)
                .subtract(dividends);

        EquityStatementResponse response = new EquityStatementResponse();
        response.setOpeningEquity(openingEquity);
        response.setContributions(contributions);
        response.setRetainedEarnings(retainedEarnings);
        response.setDividends(dividends);
        response.setClosingEquity(closingEquity);

        return response;
    }
}
