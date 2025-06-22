package com.chh.trustfort.accounting.service;


import com.chh.trustfort.accounting.Responses.ReportViewerResponse;
import com.chh.trustfort.accounting.Utility.ReportDownloadUtil;
import com.chh.trustfort.accounting.dto.ERPExportDTO;
import com.chh.trustfort.accounting.dto.FinancialSummaryDTO;
import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.service.FinancialSummaryService;
import com.chh.trustfort.accounting.service.ReportExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportExportServiceImpl implements ReportExportService {

    private final FinancialSummaryService financialSummaryService;
    private final ReportViewerService reportViewerService;

    @Override
    public ERPExportDTO prepareExport(String reportType, LocalDate startDate, LocalDate endDate) {
        FinancialSummaryDTO summary = financialSummaryService.compileSummary(startDate, endDate);

        Map<String, BigDecimal> values = new LinkedHashMap<>();

        switch (reportType.toUpperCase()) {
            case "INCOME_STATEMENT":
                values.put("totalRevenue", summary.getIncomeStatement().getRevenue());
                values.put("totalExpenses", summary.getIncomeStatement().getExpenses());
                values.put("netProfit", summary.getIncomeStatement().getNetProfit());
                break;

            case "BALANCE_SHEET":
                values.put("totalAssets", summary.getBalanceSheet().getAssets());
                values.put("totalLiabilities", summary.getBalanceSheet().getLiabilities());
                values.put("totalEquity", summary.getBalanceSheet().getEquity());
                break;

            default:
                throw new IllegalArgumentException("Unsupported reportType: " + reportType);
        }

        return ERPExportDTO.builder()
                .reportType(reportType.toUpperCase())
                .startDate(startDate)
                .endDate(endDate)
                .values(values)
                .currency(summary.getMetadata().getCurrency())
                .exportedBy(summary.getMetadata().getGeneratedBy())
                .exportedAt(summary.getMetadata().getGeneratedAt())
                .build();
    }

    @Override
    public ResponseEntity<Resource> exportReport(String reportType, String format) {
        log.info("Exporting report [{}] in format [{}]", reportType, format);

        StatementFilterDTO filter = new StatementFilterDTO();
        filter.setStartDate(LocalDate.now().minusMonths(1)); // or inject from controller/request
        filter.setEndDate(LocalDate.now()); // or inject from controller/request

        List<ReportViewerResponse> reportData = reportViewerService.getReportData(reportType, filter);


        if (reportData == null || reportData.isEmpty()) {
            throw new RuntimeException("No data available for export");
        }

        ReportDownloadUtil.ExportFormat exportFormat = ReportDownloadUtil.ExportFormat.valueOf(format.toUpperCase());
        Resource exportedFile;
        String contentType;
        String fileExtension;

        switch (exportFormat) {
            case EXCEL:
                exportedFile = (Resource) ReportDownloadUtil.exportAsExcel(reportData);
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                fileExtension = ".xlsx";
                break;
            case CSV:
                exportedFile = (Resource) ReportDownloadUtil.exportAsCsv(reportData);
                contentType = "text/csv";
                fileExtension = ".csv";
                break;
            case PDF:
                String reportTitle = reportType.toUpperCase().replace("_", " ") + " Report";
                exportedFile = (Resource) ReportDownloadUtil.exportAsPdf(reportData, reportTitle, filter.getStartDate(), filter.getEndDate());
                contentType = "application/pdf";
                fileExtension = ".pdf";
                break;

            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }

        String filename = reportType.toLowerCase() + "_report" + fileExtension;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(exportedFile);
    }
}
