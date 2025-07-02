package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ReconciliationResultDTO;
import com.chh.trustfort.accounting.dto.TaxDateRangeDTO;
import com.chh.trustfort.accounting.dto.TaxFilingSummaryDTO;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.TaxAlertService;
import com.chh.trustfort.accounting.service.TaxExportService;
import com.chh.trustfort.accounting.service.TaxFilingReportService;
import com.chh.trustfort.accounting.service.TaxReconciliationService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@RestController
@Tag(name = "Tax APIs", description = "Manage Tax Filings, Reconciliation, and Alerts")
@RequiredArgsConstructor
@Slf4j
public class TaxController {

    private final TaxFilingReportService taxFilingReportService;
    private final TaxReconciliationService taxReconciliationService;
    private final TaxExportService taxExportService;
    private final TaxAlertService taxAlertService;
    private final AesService aesService;
    private final RequestManager requestManager;

    @PostMapping(value = ApiPath.FILING_REPORT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Generate Tax Filing Report")
    public ResponseEntity<String> generateFilingReport(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        log.info("📄 Generating Tax Filing Report...");

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.VIEW_TAX_REPORT.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            return ResponseEntity.badRequest().body(aesService.encrypt(request.payload, request.appUser));
        }

        TaxDateRangeDTO filter = new Gson().fromJson(request.payload, TaxDateRangeDTO.class);
        List<TaxFilingSummaryDTO> summaries = taxFilingReportService.generateTaxFilingReport(filter.getStartDate(), filter.getEndDate());

        String encryptedResponse = aesService.encrypt(
                SecureResponseUtil.success("Tax filing report generated successfully", summaries),
                request.appUser
        );

        return ResponseEntity.ok(encryptedResponse);
    }

    @PostMapping(value = ApiPath.RECONCILIATION, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Reconcile Tax Postings for a Date Range")
    public ResponseEntity<String> reconcileTaxPostings(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        log.info("🔍 Reconciling Tax Postings...");

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.RECONCILE_TAX.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            return ResponseEntity.badRequest().body(aesService.encrypt(request.payload, request.appUser));
        }

        TaxDateRangeDTO filter = new Gson().fromJson(request.payload, TaxDateRangeDTO.class);
        String encryptedResponse = taxReconciliationService.reconcileTaxPostingsEncrypted(
                filter.getStartDate(),
                filter.getEndDate(),
                request.appUser
        );

//        String encryptedResponse = aesService.encrypt(
//                SecureResponseUtil.success("Tax reconciliation completed", results),
//                request.appUser
//        );

        return ResponseEntity.ok(encryptedResponse);
    }

    @PostMapping(value = ApiPath.EXPORT_FILING_REPORT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Export Tax Filing Report (Excel)")
    public ResponseEntity<byte[]> exportFilingReport(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        log.info("📤 Exporting Tax Filing Report to Excel...");

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.EXPORT_TAX_REPORT.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        TaxDateRangeDTO filter = new Gson().fromJson(request.payload, TaxDateRangeDTO.class);
        byte[] excelFile = taxExportService.exportTaxFilingReportToExcel(filter.getStartDate(), filter.getEndDate());

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=tax-filing-report.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelFile);
    }

    @PostMapping(value = ApiPath.ALERTS, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Trigger Tax Filing Deadline Alerts")
    public ResponseEntity<String> checkTaxAlerts(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        log.info("⏰ Triggering tax filing alert check...");

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.TAX_ALERT.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
                return ResponseEntity.badRequest().body(aesService.encrypt(request.payload, request.appUser));
        }

        taxAlertService.checkUpcomingFilingDeadlines();

        String encryptedResponse = aesService.encrypt(
                SecureResponseUtil.success("Tax alert job executed. Check logs for details", null),
                    request.appUser
        );

        return ResponseEntity.ok(encryptedResponse);
    }
}
