package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.BalanceSheetFilterRequest;
import com.chh.trustfort.accounting.dto.BalanceSheetResponse;
import com.chh.trustfort.accounting.dto.BalanceSheetResponsePayload;
import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.enums.TransactionType;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.JournalEntry;
import com.chh.trustfort.accounting.repository.JournalEntryRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceSheetService {

    private final JournalEntryRepository journalEntryRepository;
    private final AesService aesService;
    private final Gson gson;
    private final MessageSource messageSource;

    public BalanceSheetResponse generateBalanceSheet(BalanceSheetFilterRequest filter) {
        if (filter == null || filter.getStartDate() == null || filter.getEndDate() == null) {
            throw new IllegalArgumentException("Invalid or missing filter parameters");
        }

        LocalDate start = filter.getStartDate();
        LocalDate end = filter.getEndDate();

        List<JournalEntry> entries = journalEntryRepository.findByTransactionDateBetween(start, end);

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
        response.setCashAtEnd(assets); // or however you calculate cash
        return response;
    }

    public String generateEncryptedBalanceSheet(BalanceSheetFilterRequest filter, AppUser appUser) {
        BalanceSheetResponsePayload response = new BalanceSheetResponsePayload();
        response.setResponseCode("06");
        response.setResponseMessage("Balance sheet generation failed");

        try {
            BalanceSheetResponse result = generateBalanceSheet(filter);
            response.setResponseCode("00");
            response.setResponseMessage(messageSource.getMessage("balance.sheet.success", null, Locale.ENGLISH));
            response.setTotalAssets(result.getTotalAssets());
            response.setTotalLiabilities(result.getTotalLiabilities());
            response.setTotalEquity(result.getTotalEquity());
            response.setBalanced(result.getTotalAssets().subtract(result.getTotalLiabilities().add(result.getTotalEquity())));
        } catch (Exception ex) {
            log.error("⚠️ Error generating balance sheet: {}", ex.getMessage());
        }

        return aesService.encrypt(gson.toJson(response), appUser);
    }
}
