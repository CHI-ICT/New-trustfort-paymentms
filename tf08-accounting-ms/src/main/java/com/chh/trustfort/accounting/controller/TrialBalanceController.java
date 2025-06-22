package com.chh.trustfort.accounting.controller;

import org.springframework.core.io.Resource; // ✅ Correct

import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.Responses.ReportViewerResponse;
import com.chh.trustfort.accounting.Utility.ReportDownloadUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.*;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@EncryptResponse
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Trial Balance", description = "Endpoints for generating trial balance reports")
@Slf4j
public class TrialBalanceController {

    private final TrialBalanceService trialBalanceService;
    private final FinancialSummaryService financialSummaryService;
    private final ReportExportService reportExportService;
    private final ReportViewerService reportViewerService;

    private final VarianceAnalysisService varianceAnalysisService;
    private final DashboardReportService dashboardReportService;
    private final RealTimeMonitoringService realTimeMonitoringService;
    private final ReportAuditTrailService auditTrailService;
    private final RequestManager requestManager;
    private final AesService aesService;


@GetMapping(value = ApiPath.GET_TRIAL_BALANCE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TrialBalanceResponse>> getTrialBalance(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.info("Generating trial balance from {} to {}", startDate, endDate);
        List<TrialBalanceResponse> response = trialBalanceService.generateTrialBalance(startDate, endDate);
        return ResponseEntity.ok(response);
    }


    @GetMapping(value = ApiPath.GET_FINANCIAL_SUMMARY, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FinancialSummaryDTO> getSummary(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        FinancialSummaryDTO summary = financialSummaryService.compileSummary(startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping(value = ApiPath.EXPORT_REPORT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ERPExportDTO> exportReport(
            @RequestParam("reportType") String reportType,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        ERPExportDTO exportDTO = reportExportService.prepareExport(reportType, startDate, endDate);
        return ResponseEntity.ok(exportDTO);
    }

    @GetMapping(value = ApiPath.DOWNLOAD_REPORT, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadReport(
            @RequestParam("reportType") String reportType,
            @RequestParam("format") String format,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        // Inject the date range into StatementFilterDTO
        StatementFilterDTO filter = new StatementFilterDTO();
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);

        List<ReportViewerResponse> reportData = reportViewerService.getReportData(reportType, filter);

        if (reportData == null || reportData.isEmpty()) {
            throw new RuntimeException("No data available for export");
        }

        String fileExtension;
        String contentType;
        Resource exportedFile;
        ReportDownloadUtil.ExportFormat exportFormat = ReportDownloadUtil.ExportFormat.valueOf(format.toUpperCase());

        String reportTitle = reportType.toUpperCase().replace("_", " ") + " Report";

        switch (exportFormat) {
            case PDF:
                exportedFile = (Resource) ReportDownloadUtil.exportAsPdf(reportData, reportTitle, startDate, endDate);
                contentType = "application/pdf";
                fileExtension = ".pdf";
                break;
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
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }

        String filename = reportType.toLowerCase() + "_report" + fileExtension;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(exportedFile);
    }

    @GetMapping(value = ApiPath.GET_VARIANCE_ANALYSIS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VarianceAnalysisDTO> getVarianceReport(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(varianceAnalysisService.analyzeVariance(startDate, endDate));
    }


    @GetMapping(value = ApiPath.GET_DASHBOARD_SUMMARY, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(dashboardReportService.generateDashboardView(startDate, endDate));
    }

    @GetMapping(value = ApiPath.GET_REPORT_MONITORING, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RealTimeMonitoringDTO> monitorTrends(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate prevStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate prevEnd,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currEnd
    ) {
        return ResponseEntity.ok(realTimeMonitoringService.monitorTrends(prevStart, prevEnd, currStart, currEnd));
    }

}
