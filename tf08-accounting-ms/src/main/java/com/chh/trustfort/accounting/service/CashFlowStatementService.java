package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.Responses.ReportViewerResponse;
import com.chh.trustfort.accounting.dto.CashFlowStatementDTO;
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
@RequiredArgsConstructor
@Slf4j
public class CashFlowStatementService {

    private final JournalEntryRepository journalEntryRepository;

    public CashFlowStatementDTO generateCashFlowStatement(StatementFilterDTO filter) {
        List<JournalEntry> entries = journalEntryRepository.findByTransactionDateBetween(
                filter.getStartDate(), filter.getEndDate());

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

        BigDecimal operating = BigDecimal.ZERO;
        BigDecimal investing = BigDecimal.ZERO;
        BigDecimal financing = BigDecimal.ZERO;

        for (JournalEntry entry : entries) {
            AccountClassification classification = entry.getAccount().getClassification();
            String accountName = entry.getAccount().getAccountName().toLowerCase();
            TransactionType type = entry.getTransactionType();
            BigDecimal amount = entry.getAmount();

            BigDecimal signedAmount;

            switch (classification) {
                case REVENUE:
                case EXPENSE:
                    signedAmount = type == TransactionType.DEBIT ? amount : amount.negate();
                    operating = operating.add(signedAmount);
                    break;

                case ASSET:
                    if (!accountName.contains("cash")) {
                        signedAmount = type == TransactionType.DEBIT ? amount : amount.negate();
                        investing = investing.add(signedAmount);
                    }
                    break;

                case LIABILITY:
                case EQUITY:
                    signedAmount = type == TransactionType.CREDIT ? amount : amount.negate();
                    financing = financing.add(signedAmount);
                    break;

                default:
                    break;
            }
        }

        BigDecimal netCashFlow = operating.add(investing).add(financing);
        BigDecimal openingCashBalance = computeOpeningCashBalance(filter);
        BigDecimal closingCashBalance = openingCashBalance.add(netCashFlow);

        return CashFlowStatementDTO.builder()
                .operatingActivities(operating)
                .investingActivities(investing)
                .financingActivities(financing)
                .netCashFlow(netCashFlow)
                .openingCashBalance(openingCashBalance)
                .closingCashBalance(closingCashBalance)
                .build();
    }

    private BigDecimal computeOpeningCashBalance(StatementFilterDTO filter) {
        List<JournalEntry> entries = journalEntryRepository.findByTransactionDateBetween(
                LocalDate.of(2000, 1, 1), filter.getStartDate().minusDays(1));

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

        return entries.stream()
                .filter(e -> e.getAccount().getAccountName().toLowerCase().contains("cash"))
                .map(e -> e.getTransactionType() == TransactionType.DEBIT ? e.getAmount() : e.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<ReportViewerResponse> generateCashFlowForViewer(StatementFilterDTO filter) {
        CashFlowStatementDTO dto = generateCashFlowStatement(filter);
        BigDecimal netChange = dto.getClosingCashBalance().subtract(dto.getOpeningCashBalance());

        return List.of(
                new ReportViewerResponse(Map.of("Activity", "Cash flows from Operating Activities", "Amount", dto.getOperatingActivities())),
                new ReportViewerResponse(Map.of("Activity", "Cash flows from Investing Activities", "Amount", dto.getInvestingActivities())),
                new ReportViewerResponse(Map.of("Activity", "Cash flows from Financing Activities", "Amount", dto.getFinancingActivities())),
                new ReportViewerResponse(Map.of("Activity", "Net Increase/(Decrease) in Cash", "Amount", netChange)),
                new ReportViewerResponse(Map.of("Activity", "Cash and Cash Equivalents at Beginning of Period", "Amount", dto.getOpeningCashBalance())),
                new ReportViewerResponse(Map.of("Activity", "Cash and Cash Equivalents at End of Period", "Amount", dto.getClosingCashBalance()))
        );
    }

}
