package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.Responses.ReportViewerResponse;
import com.chh.trustfort.accounting.dto.*;
import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.enums.ReportType;
import com.chh.trustfort.accounting.enums.TransactionType;
import com.chh.trustfort.accounting.model.JournalEntry;
import com.chh.trustfort.accounting.repository.JournalEntryRepository;
import com.chh.trustfort.accounting.service.BalanceSheetService;
import com.chh.trustfort.accounting.service.FinancialReportService;
import com.chh.trustfort.accounting.service.IncomeStatementService;
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
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class FinancialReportServiceImpl implements FinancialReportService {

    private final JournalEntryRepository journalEntryRepository;
    private final IncomeStatementService incomeStatementService;
    private final BalanceSheetService balanceSheetService;

    @Override
    public FinancialReportResponse generateProfitAndLoss(LocalDate start, LocalDate end) {
        List<JournalEntry> revenues = journalEntryRepository.findByClassificationAndDateRange(AccountClassification.REVENUE, start, end);
        List<JournalEntry> expenses = journalEntryRepository.findByClassificationAndDateRange(AccountClassification.EXPENSE, start, end);

        List<ReportLineItem> items = new ArrayList<>();
        BigDecimal totalRevenue = sumEntries(revenues, items, "Revenue");
        BigDecimal totalExpense = sumEntries(expenses, items, "Expense");

        BigDecimal netProfit = totalRevenue.subtract(totalExpense);

        return FinancialReportResponse.builder()
                .status("success")
                .message("Profit and Loss report generated successfully")
                .reportType(ReportType.valueOf("PROFIT_AND_LOSS"))
                .startDate(start)
                .endDate(end)
                .lineItems(items)
                .total(netProfit)
                .build();
    }

    private BigDecimal sumEntries(List<JournalEntry> entries, List<ReportLineItem> items, String label) {
        return entries.stream()
                .collect(Collectors.groupingBy(e -> e.getAccount().getAccountCode(), Collectors.reducing(BigDecimal.ZERO, JournalEntry::getAmount, BigDecimal::add)))
                .entrySet().stream().map(entry -> {
                    String accountCode = (String) entry.getKey();
                    BigDecimal amount = entry.getValue();
                    items.add(new ReportLineItem(label, amount, accountCode, null));
                    return amount;
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BalanceSheetReportDTO generateBalanceSheet(LocalDate asOfDate) {
        BigDecimal totalAssets = journalEntryRepository.sumByClassification(AccountClassification.ASSET, asOfDate);
        BigDecimal totalLiabilities = journalEntryRepository.sumByClassification(AccountClassification.LIABILITY, asOfDate);
        BigDecimal totalEquity = journalEntryRepository.sumByClassification(AccountClassification.EQUITY, asOfDate);

        List<JournalEntry> assetEntries = journalEntryRepository.findByClassificationAndDate(AccountClassification.ASSET, asOfDate);
        List<JournalEntry> liabilityEntries = journalEntryRepository.findByClassificationAndDate(AccountClassification.LIABILITY, asOfDate);
        List<JournalEntry> equityEntries = journalEntryRepository.findByClassificationAndDate(AccountClassification.EQUITY, asOfDate);

        return BalanceSheetReportDTO.builder()
                .asOfDate(asOfDate)
                .totalAssets(totalAssets)
                .totalLiabilities(totalLiabilities)
                .totalEquity(totalEquity)
                .totalLiabilitiesAndEquity(totalLiabilities.add(totalEquity))
                .lineItems(Stream.of(assetEntries, liabilityEntries, equityEntries)
                        .flatMap(List::stream)
                        .collect(Collectors.groupingBy(
                                e -> e.getAccount().getAccountName(),
                                Collectors.reducing(BigDecimal.ZERO, JournalEntry::getAmount, BigDecimal::add)
                        ))
                        .entrySet().stream()
                        .map(e -> new BalanceSheetLineItemDTO("Balance Sheet", e.getKey(), e.getValue()))
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public FinancialReportResponse generateCashFlow(LocalDate startDate, LocalDate endDate) {
        log.info("Generating Cash Flow from {} to {}", startDate, endDate);

        List<JournalEntry> entries = journalEntryRepository.findByTransactionDateBetween(startDate, endDate);

        List<CashFlowLineItemDTO> lineItems = new ArrayList<>();
        BigDecimal totalInflow = BigDecimal.ZERO;
        BigDecimal totalOutflow = BigDecimal.ZERO;

        for (JournalEntry entry : entries) {
            String section = mapToCashFlowSection(entry.getAccount().getClassification());
            BigDecimal signedAmount = entry.getTransactionType() == TransactionType.CREDIT
                    ? entry.getAmount()
                    : entry.getAmount().negate();

            if (signedAmount.compareTo(BigDecimal.ZERO) > 0) {
                totalInflow = totalInflow.add(signedAmount);
            } else {
                totalOutflow = totalOutflow.add(signedAmount.abs());
            }

            lineItems.add(CashFlowLineItemDTO.builder()
                    .section(section)
                    .accountName(entry.getAccount().getAccountName())
                    .amount(signedAmount)
                    .build());
        }

        return FinancialReportResponse.builder()
                .status("success")
                .message("Cash Flow Report generated successfully")
                .reportType(ReportType.CASH_FLOW)
                .startDate(startDate)
                .endDate(endDate)
                .lineItem(lineItems)
                .totalCashInflow(totalInflow)
                .totalCashOutflow(totalOutflow)
                .netCashFlow(totalInflow.subtract(totalOutflow))
                .build();
    }

    private String mapToCashFlowSection(AccountClassification classification) {
        switch (classification) {
            case REVENUE:
            case EXPENSE:
                return "Operating Activities";
            case ASSET:
                return "Investing Activities";
            case LIABILITY:
            case EQUITY:
                return "Financing Activities";
            default:
                return "Uncategorized";
        }
    }

    @Override
    public FinancialReportResponse generateConsolidatedSummary(LocalDate startDate, LocalDate endDate) {
        log.info("Generating consolidated summary from {} to {}", startDate, endDate);

        BigDecimal totalAssets = journalEntryRepository.sumAmountByClassificationAndDateRange(AccountClassification.ASSET, startDate, endDate);
        BigDecimal totalLiabilities = journalEntryRepository.sumAmountByClassificationAndDateRange(AccountClassification.LIABILITY, startDate, endDate);
        BigDecimal totalEquity = journalEntryRepository.sumAmountByClassificationAndDateRange(AccountClassification.EQUITY, startDate, endDate);
        BigDecimal totalRevenue = journalEntryRepository.sumAmountByClassificationAndDateRange(AccountClassification.REVENUE, startDate, endDate);
        BigDecimal totalExpenses = journalEntryRepository.sumAmountByClassificationAndDateRange(AccountClassification.EXPENSE, startDate, endDate);

        BigDecimal netIncome = totalRevenue.subtract(totalExpenses);

        ConsolidatedSummaryDTO dto = ConsolidatedSummaryDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalAssets(totalAssets)
                .totalLiabilities(totalLiabilities)
                .totalEquity(totalEquity)
                .totalRevenue(totalRevenue)
                .totalExpenses(totalExpenses)
                .netIncome(netIncome)
                .build();

        return FinancialReportResponse.builder()
                .status("success")
                .message("Consolidated summary generated successfully")
                .reportType(ReportType.CONSOLIDATED_REPORT)
                .startDate(startDate)
                .endDate(endDate)
                .total(totalAssets.add(totalLiabilities).add(totalEquity))
                .lineItems(List.of(
                        ReportLineItem.builder().label("Total Assets").amount(totalAssets).build(),
                        ReportLineItem.builder().label("Total Liabilities").amount(totalLiabilities).build(),
                        ReportLineItem.builder().label("Total Equity").amount(totalEquity).build(),
                        ReportLineItem.builder().label("Total Revenue").amount(totalRevenue).build(),
                        ReportLineItem.builder().label("Total Expenses").amount(totalExpenses).build(),
                        ReportLineItem.builder().label("Net Income").amount(netIncome).build()
                ))
                .build();
    }

    @Override
    public List<ReportViewerResponse> generateConsolidatedReportForViewer(LocalDate startDate, LocalDate endDate) {
        FinancialReportResponse response = generateConsolidatedSummary(startDate, endDate);

        List<ReportViewerResponse> viewerResponses = new ArrayList<>();
        for (ReportLineItem item : response.getLineItems()) {
            Map<String, Object> fields = new LinkedHashMap<>();
            fields.put("Metric", item.getLabel());
            fields.put("Amount", item.getAmount());
            viewerResponses.add(new ReportViewerResponse(fields));
        }
        return viewerResponses;
    }
}
