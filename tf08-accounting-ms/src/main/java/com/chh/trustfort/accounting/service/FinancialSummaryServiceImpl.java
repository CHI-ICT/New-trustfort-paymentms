package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.FinancialSummaryDTO;
import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FinancialSummaryServiceImpl implements FinancialSummaryService {

    private final JournalEntryRepository journalEntryRepository;

    @Override
    public FinancialSummaryDTO compileSummary(LocalDate start, LocalDate end) {
        BigDecimal revenue = journalEntryRepository.sumAmountByClassificationAndDateRange(AccountClassification.REVENUE, start, end);
        BigDecimal expenses = journalEntryRepository.sumAmountByClassificationAndDateRange(AccountClassification.EXPENSE, start, end);
        BigDecimal netProfit = revenue.subtract(expenses);
        BigDecimal assets = journalEntryRepository.sumAmountByClassificationAndDateRange(AccountClassification.ASSET, start, end);
        BigDecimal liabilities = journalEntryRepository.sumAmountByClassificationAndDateRange(AccountClassification.LIABILITY, start, end);
        BigDecimal equity = journalEntryRepository.sumAmountByClassificationAndDateRange(AccountClassification.EQUITY, start, end);

        return FinancialSummaryDTO.builder()
                .startDate(start)
                .endDate(end)
                .incomeStatement(FinancialSummaryDTO.SummarySection.builder()
                        .revenue(revenue)
                        .expenses(expenses)
                        .netProfit(netProfit)
                        .build())
                .balanceSheet(FinancialSummaryDTO.SummarySection.builder()
                        .assets(assets)
                        .liabilities(liabilities)
                        .equity(equity)
                        .build())
                .metadata(FinancialSummaryDTO.SummaryMetadata.builder()
                        .currency("NGN")
                        .generatedBy("System Admin")
                        .generatedAt(LocalDateTime.now())
                        .build())
                .build();
    }
}
