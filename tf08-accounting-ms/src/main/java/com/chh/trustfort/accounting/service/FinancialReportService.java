package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.Responses.ReportViewerResponse;
import com.chh.trustfort.accounting.dto.BalanceSheetReportDTO;
import com.chh.trustfort.accounting.dto.FinancialReportResponse;
import com.chh.trustfort.accounting.enums.ReportType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface FinancialReportService {
    FinancialReportResponse generateProfitAndLoss(LocalDate startDate, LocalDate endDate);
    BalanceSheetReportDTO generateBalanceSheet(LocalDate asOfDate);
    FinancialReportResponse generateCashFlow(LocalDate startDate, LocalDate endDate);
    FinancialReportResponse generateConsolidatedSummary(LocalDate startDate, LocalDate endDate);

    List<ReportViewerResponse> generateConsolidatedReportForViewer(LocalDate startDate, LocalDate endDate);
//    FinancialReportResponse generateReport(ReportType reportType, LocalDate startDate, LocalDate endDate);
//    FinancialReportResponse generateEquityStatement(LocalDate startDate, LocalDate endDate);

}
