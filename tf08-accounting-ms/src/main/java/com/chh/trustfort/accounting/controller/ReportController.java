package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.model.AppUser;
import com.google.gson.Gson;
import org.springframework.core.io.Resource; // ✅ Correct

import com.chh.trustfort.accounting.Responses.ReportViewerResponse;
import com.chh.trustfort.accounting.Utility.ReportDownloadUtil;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.*;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Trial Balance", description = "Endpoints for generating trial balance reports")
@Slf4j
public class ReportController {

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
    private final Gson gson;

//    @PostMapping(value = ApiPath.GET_TRIAL_BALANCE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<String> getTrialBalance(
//            @RequestParam String idToken,
//            @RequestBody String requestPayload,
//            HttpServletRequest httpRequest
//    ) {
//        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
//                Role.TRIAL_BALANCE_VIEW.getValue(), requestPayload, httpRequest, idToken
//        );
//
//        if (request.isError) {
//            return ResponseEntity.badRequest().body(aesService.encrypt(request.payload, request.appUser));
//        }
//
//        TaxDateRangeDTO filter = gson.fromJson(request.payload, TaxDateRangeDTO.class);
//        List<TrialBalanceResponse> response = trialBalanceService.generateTrialBalance(filter.getStartDate(), filter.getEndDate());
//        return ResponseEntity.ok(aesService.encrypt(SecureResponseUtil.success("Trial balance generated", response), request.appUser));
//    }

    @PostMapping(value = ApiPath.GET_FINANCIAL_SUMMARY, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getSummary(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.FINANCIAL_SUMMARY_VIEW.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            return ResponseEntity.badRequest().body(aesService.encrypt(request.payload, request.appUser));
        }

        TaxDateRangeDTO filter = gson.fromJson(request.payload, TaxDateRangeDTO.class);
        FinancialSummaryDTO summary = financialSummaryService.compileSummary(filter.getStartDate(), filter.getEndDate());
        return ResponseEntity.ok(aesService.encrypt(SecureResponseUtil.success("Financial summary compiled", summary), request.appUser));
    }

    @PostMapping(value = ApiPath.EXPORT_REPORT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> exportReport(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.REPORT_EXPORT.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            return ResponseEntity.badRequest().body(aesService.encrypt(request.payload, request.appUser));
        }

        ReportExportRequestDTO dto = gson.fromJson(request.payload, ReportExportRequestDTO.class);
        ERPExportDTO exportDTO = reportExportService.prepareExport(dto.getReportType(), dto.getStartDate(), dto.getEndDate());
        return ResponseEntity.ok(aesService.encrypt(SecureResponseUtil.success("Report export ready", exportDTO), request.appUser));
    }

    @PostMapping(value = ApiPath.GET_VARIANCE_ANALYSIS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getVarianceReport(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.VARIANCE_ANALYSIS_VIEW.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            return ResponseEntity.badRequest().body(aesService.encrypt(request.payload, request.appUser));
        }

        TaxDateRangeDTO filter = gson.fromJson(request.payload, TaxDateRangeDTO.class);
        VarianceAnalysisDTO variance = varianceAnalysisService.analyzeVariance(filter.getStartDate(), filter.getEndDate());
        return ResponseEntity.ok(aesService.encrypt(SecureResponseUtil.success("Variance analysis report", variance), request.appUser));
    }

    @PostMapping(value = ApiPath.GET_DASHBOARD_SUMMARY, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getDashboardSummary(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.DASHBOARD_VIEW.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
                return ResponseEntity.badRequest().body(aesService.encrypt(request.payload, request.appUser));
        }

        TaxDateRangeDTO filter = gson.fromJson(request.payload, TaxDateRangeDTO.class);
        DashboardSummaryDTO dashboard = dashboardReportService.generateDashboardView(filter.getStartDate(), filter.getEndDate());
        return ResponseEntity.ok(aesService.encrypt(SecureResponseUtil.success("Dashboard summary ready", dashboard), request.appUser));
    }

    @PostMapping(value = ApiPath.GET_REPORT_MONITORING, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> monitorTrends(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.REPORT_MONITOR.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            return ResponseEntity.badRequest().body(aesService.encrypt(request.payload, request.appUser));
        }

        TrendDateRangeDTO dto = gson.fromJson(request.payload, TrendDateRangeDTO.class);
        RealTimeMonitoringDTO trends = realTimeMonitoringService.monitorTrends(
                dto.getPrevStart(), dto.getPrevEnd(), dto.getCurrStart(), dto.getCurrEnd()
        );
        return ResponseEntity.ok(aesService.encrypt(SecureResponseUtil.success("Monitoring report", trends), request.appUser));
    }

    @GetMapping(value = ApiPath.DOWNLOAD_REPORT, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadReport(
            @RequestHeader(value = "idToken", required = false) String idToken,
            @RequestHeader("reportType") String reportType,
            @RequestHeader("format") String format,
            @RequestHeader("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestHeader("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.DOWNLOAD_REPORT.getValue(), null, httpRequest, idToken
        );

        if (request.isError) {
            return ResponseEntity.badRequest().build();
        }

        StatementFilterDTO filter = new StatementFilterDTO();
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);

        List<ReportViewerResponse> reportData = reportViewerService.getReportData(reportType, filter);
        if (reportData == null || reportData.isEmpty()) {
            throw new RuntimeException("No data available for export");
        }

        String reportTitle = reportType.toUpperCase().replace("_", " ") + " Report";
        String fileExtension;
        String contentType;
        Resource exportedFile;
        ReportDownloadUtil.ExportFormat exportFormat = ReportDownloadUtil.ExportFormat.valueOf(format.toUpperCase());

        switch (exportFormat) {
            case PDF:
                exportedFile = ReportDownloadUtil.exportAsPdf(reportData, reportTitle, startDate, endDate);
                contentType = "application/pdf";
                fileExtension = ".pdf";
                break;
            case EXCEL:
                exportedFile = ReportDownloadUtil.exportAsExcel(reportData);
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                fileExtension = ".xlsx";
                break;
            case CSV:
                exportedFile = ReportDownloadUtil.exportAsCsv(reportData);
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

}


