package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.*;
import com.chh.trustfort.accounting.enums.ReportType;
import com.chh.trustfort.accounting.service.FinancialReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@EncryptResponse
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Financial Reports", description = "Endpoints for generating financial reports")
@Slf4j
public class FinancialReportController {

    private final FinancialReportService reportService;

//    @GetMapping("/reports/generate")
//    public ResponseEntity<ApiResponse> generateReport(
//            @RequestParam ReportType reportType,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//
//        FinancialReportResponse response = reportService.generateReport(reportType, startDate, endDate);
//        return ResponseEntity.ok(ApiResponse.success(response));
//    }
    @GetMapping(value = ApiPath.GET_PROFIT_AND_LOSS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FinancialReportResponse> getProfitAndLoss(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.generateProfitAndLoss(startDate, endDate));
    }

    @GetMapping(value = ApiPath.GET_BALANCE_SHEET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BalanceSheetReportDTO> getBalanceSheet(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        return ResponseEntity.ok(reportService.generateBalanceSheet(asOfDate));
    }

    @GetMapping(value = ApiPath.GET_CASH_FLOW, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FinancialReportResponse> getCashFlow(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.generateCashFlow(startDate, endDate));
    }

    @GetMapping(value = ApiPath.GET_CONSOLIDATED_SUMMARY, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FinancialReportResponse> getConsolidatedSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.generateConsolidatedSummary(startDate, endDate));
    }

    // Optional: Add endpoint for exporting (PDF/Excel) later using ?format=pdf or format=excel
}
