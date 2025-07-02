package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.Responses.ReportViewerResponse;
import com.chh.trustfort.accounting.dto.BalanceSheetLineItemDTO;
import com.chh.trustfort.accounting.dto.BalanceSheetReportDTO;
import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.enums.ReportType;
import com.chh.trustfort.accounting.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReportViewerServiceImpl implements ReportViewerService {

    private final TrialBalanceService trialBalanceService;
    private final IncomeStatementService incomeStatementService;
    private final CashFlowStatementService cashFlowStatementService;
    private final EquityStatementService equityStatementService;
    //    private final ConsolidatedReportService consolidatedReportService;
    private final FinancialReportService financialReportService;



    @Override
    public List<ReportViewerResponse> getReportData(String reportTypeStr, StatementFilterDTO filter) {
        ReportType reportType = ReportType.valueOf(reportTypeStr.toUpperCase());

        switch (reportType) {
            case TRIAL_BALANCE:
                return trialBalanceService.generateTrialBalanceForViewer(filter.getStartDate(), filter.getEndDate());

            case INCOME_STATEMENT:
                return incomeStatementService.generateIncomeStatementForViewer(filter);

            case CASH_FLOW:
                return cashFlowStatementService.generateCashFlowForViewer(filter);

            case EQUITY_STATEMENT:
                return equityStatementService.generateEquityStatementForViewer(filter);

            case BALANCE_SHEET:
                return generateBalanceSheetViewerData(filter);

            case CONSOLIDATED_REPORT:
                return financialReportService.generateConsolidatedReportForViewer(filter.getStartDate(), filter.getEndDate());

            default:
                throw new IllegalArgumentException("Unsupported report type: " + reportType);
        }
    }

    public List<ReportViewerResponse> generateBalanceSheetViewerData(StatementFilterDTO filter) {
        LocalDate asOfDate = filter.getEndDate(); // Use endDate as asOfDate
        BalanceSheetReportDTO dto = financialReportService.generateBalanceSheet(asOfDate);

        List<ReportViewerResponse> responses = new ArrayList<>();

        // Add summary line
        Map<String, Object> summaryFields = new LinkedHashMap<>();
        summaryFields.put("As of Date", dto.getAsOfDate());
        summaryFields.put("Total Assets", dto.getTotalAssets());
        summaryFields.put("Total Liabilities", dto.getTotalLiabilities());
        summaryFields.put("Total Equity", dto.getTotalEquity());
        summaryFields.put("Total Liabilities + Equity", dto.getTotalLiabilitiesAndEquity());

        ReportViewerResponse summaryRow = new ReportViewerResponse();
        summaryRow.setFields(summaryFields);
        responses.add(summaryRow);

        // Add each line item
        for (BalanceSheetLineItemDTO item : dto.getLineItems()) {
            Map<String, Object> line = new LinkedHashMap<>();
            line.put("Account Name", item.getAccountName());
            line.put("Amount", item.getAmount());

            ReportViewerResponse itemRow = new ReportViewerResponse();
            itemRow.setFields(line);
            responses.add(itemRow);
        }

        return responses;
    }

}