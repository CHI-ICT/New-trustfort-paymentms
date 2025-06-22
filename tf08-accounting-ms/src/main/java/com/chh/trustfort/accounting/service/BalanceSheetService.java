package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.BalanceSheetFilterRequest;
import com.chh.trustfort.accounting.dto.BalanceSheetResponse;
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
public class BalanceSheetService {

    private final JournalEntryRepository journalEntryRepository;

    public BalanceSheetResponse generateBalanceSheet(BalanceSheetFilterRequest filter) {
        LocalDate start = filter.getStartDate();
        LocalDate end = filter.getEndDate();
        String department = filter.getDepartment();
        String businessUnit = filter.getBusinessUnit();

        // use these for filtering journal entries, etc.
    // ✅ Get entries within date range
        List<JournalEntry> entries = journalEntryRepository.findByTransactionDateBetween(start, end);

        // ✅ Optional filtering by department and business unit
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

        BigDecimal assets = BigDecimal.ZERO;
        BigDecimal liabilities = BigDecimal.ZERO;
        BigDecimal equity = BigDecimal.ZERO;

        for (JournalEntry entry : entries) {
            BigDecimal signedAmount;

            switch (entry.getAccount().getClassification()) {
                case ASSET:
                    signedAmount = entry.getTransactionType() == TransactionType.DEBIT
                            ? entry.getAmount()
                            : entry.getAmount().negate();
                    assets = assets.add(signedAmount);
                    break;

                case LIABILITY:
                case EQUITY:
                    signedAmount = entry.getTransactionType() == TransactionType.CREDIT
                            ? entry.getAmount()
                            : entry.getAmount().negate();

                    if (entry.getAccount().getClassification() == AccountClassification.LIABILITY) {
                        liabilities = liabilities.add(signedAmount);
                    } else {
                        equity = equity.add(signedAmount);
                    }
                    break;
            }

        }

        BalanceSheetResponse response = new BalanceSheetResponse();
        response.setTotalAssets(assets);
        response.setTotalLiabilities(liabilities);
        response.setTotalEquity(equity);
        response.setIsBalanced(assets.subtract(liabilities.add(equity)));

        return response;
    }
}
