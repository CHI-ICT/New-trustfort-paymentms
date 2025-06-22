package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ReconciliationResultDTO;
import com.chh.trustfort.accounting.dto.TaxFilingSummaryDTO;
import com.chh.trustfort.accounting.service.TaxAlertService;
import com.chh.trustfort.accounting.service.TaxExportService;
import com.chh.trustfort.accounting.service.TaxFilingReportService;
import com.chh.trustfort.accounting.service.TaxReconciliationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(ApiPath.BASE_API)
@Tag(name = "Tax APIs", description = "Manage Tax Filings, Reconciliation, and Alerts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TaxController {

    private final TaxFilingReportService taxFilingReportService;
    private final TaxReconciliationService taxReconciliationService;
    private final TaxExportService taxExportService;
    private final TaxAlertService taxAlertService;

    @PostMapping(value = ApiPath.FILING_REPORT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Generate Tax Filing Report")
    public ResponseEntity<List<TaxFilingSummaryDTO>> generateFilingReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<TaxFilingSummaryDTO> summaries = taxFilingReportService.generateTaxFilingReport(startDate, endDate);
        return ResponseEntity.ok(summaries);
    }
    @GetMapping(ApiPath.RECONCILIATION)
    @Operation(summary = "Reconcile Tax Postings for a Date Range")
    public ResponseEntity<List<ReconciliationResultDTO>> reconcileTaxPostings(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<ReconciliationResultDTO> results = taxReconciliationService.reconcileTaxPostings(startDate, endDate);
        return ResponseEntity.ok(results);
    }

    @GetMapping(ApiPath.EXPORT_FILING_REPORT)
    @Operation(summary = "Export Tax Filing Report (Excel)")
    public ResponseEntity<byte[]> exportFilingReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        byte[] excelFile = taxExportService.exportTaxFilingReportToExcel(startDate, endDate);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=tax-filing-report.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelFile);
    }

    @GetMapping(ApiPath.ALERTS)
    @Operation(summary = "Get Tax Filing Deadline Alerts")
    public ResponseEntity<String> checkTaxAlerts() {
        taxAlertService.checkUpcomingFilingDeadlines();
        return ResponseEntity.ok("Tax filing deadline check triggered successfully. Check logs for alerts.");
    }
}
