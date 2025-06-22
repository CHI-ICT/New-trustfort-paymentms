//package com.chh.trustfort.accounting.controller;
//
//
//import com.chh.trustfort.accounting.constant.ApiPath;
//import com.chh.trustfort.accounting.dto.*;
//import com.chh.trustfort.accounting.service.*;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import lombok.RequiredArgsConstructor;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.ByteArrayOutputStream;
//import java.time.LocalDate;
//import java.util.List;
//
//@RestController
//@RequestMapping(ApiPath.BASE_API)
//@Tag(name = "Financial Reports", description = "Generate financial reports and integrity checks")
//@RequiredArgsConstructor
//@SecurityRequirement(name = "bearerAuth")
//public class FinancialReportController {
//
//    private final IncomeStatementService incomeStatementService;
//    private final BalanceSheetService balanceSheetService;
//    private final CashFlowStatementService cashFlowStatementService;
//    private final EquityStatementService equityStatementService;
//    private final StatementIntegrityService statementIntegrityService;
//    private final IncomeStatementExportService incomeStatementExportService;
//    private final BalanceSheetExportService balanceSheetExportService;
//    private final CashFlowStatementExportService cashFlowStatementExportService;
//    private final EquityStatementExportService equityStatementExportService;
//
//
//
//
//
//    @PostMapping(value = ApiPath.GENERATE_INCOME_STATEMENT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @Operation(summary = "Generate Income Statement")
//    public ResponseEntity<IncomeStatementResponse> generateIncomeStatement(@RequestBody StatementFilterDTO filter) {
//        return ResponseEntity.ok(incomeStatementService.generateIncomeStatement(filter));
//    }
//
//
//    @GetMapping(ApiPath.EXPORT_INCOME_STATEMENT)
//    @Operation(summary = "Export Income Statement to PDF or Excel")
//    public ResponseEntity<byte[]> exportIncomeStatement(
//            @RequestParam String format,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
//    ) {
//        StatementFilterDTO filter = new StatementFilterDTO();
//        filter.setStartDate(startDate);
//        filter.setEndDate(endDate);
//
//        if (format.equalsIgnoreCase("pdf")) {
//            byte[] pdfBytes = incomeStatementExportService.exportToPdf(filter);
//            return buildFileResponse(pdfBytes, "income-statement", "pdf");
//        } else if (format.equalsIgnoreCase("excel")) {
//            byte[] excelBytes = incomeStatementExportService.exportToExcel(filter);
//            return buildFileResponse(excelBytes, "income-statement", "xlsx");
//        } else {
//            return ResponseEntity.badRequest().body(null);
//        }
//    }
//
//    private ResponseEntity<byte[]> buildFileResponse(byte[] content, String filenamePrefix, String extension) {
//        String filename = filenamePrefix + "_" + LocalDate.now() + "." + extension;
//
//        return ResponseEntity.ok()
//                .header("Content-Disposition", "attachment; filename=" + filename)
//                .header("Content-Type", extension.equals("pdf")
//                        ? "application/pdf"
//                        : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
//                .body(content);
//    }
//
//
//
//    @PostMapping(value = ApiPath.GENERATE_BALANCE_SHEET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @Operation(summary = "Generate Balance Sheet")
//    public ResponseEntity<BalanceSheetResponse> generateBalanceSheet(@RequestBody BalanceSheetFilterRequest filter) {
//        return ResponseEntity.ok(balanceSheetService.generateBalanceSheet(filter));
//    }
//    @GetMapping(ApiPath.EXPORT_BALANCE_SHEET)
//    @Operation(summary = "Export Balance Sheet to PDF or Excel")
//    public ResponseEntity<byte[]> exportBalanceSheet(
//            @RequestParam String format,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
//    ) {
//        BalanceSheetFilterRequest filter = new BalanceSheetFilterRequest();
//        filter.setStartDate(startDate);
//        filter.setEndDate(endDate);
//
//        if (format.equalsIgnoreCase("pdf")) {
//            byte[] pdfBytes = balanceSheetExportService.exportToPdf(filter);
//            return buildFileResponse(pdfBytes, "balance-sheet", "pdf");
//        } else if (format.equalsIgnoreCase("excel")) {
//            byte[] excelBytes = balanceSheetExportService.exportToExcel(filter);
//            return buildFileResponse(excelBytes, "balance-sheet", "xlsx");
//        } else {
//            return ResponseEntity.badRequest().body(null);
//        }
//    }
//
//
//
//    @PostMapping(value = ApiPath.CASH_FLOW_STATEMENT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @Operation(summary = "Generate Cash Flow Statement")
//    public ResponseEntity<CashFlowStatementDTO> generateCashFlow(@RequestBody StatementFilterDTO filter) {
//        return ResponseEntity.ok(cashFlowStatementService.generateCashFlowStatement(filter));
//    }
//
//    @GetMapping(ApiPath.EXPORT_CASH_FLOW)
//    @Operation(summary = "Export Cash Flow Statement to PDF or Excel")
//    public ResponseEntity<byte[]> exportCashFlow(
//            @RequestParam String format,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
//    ) {
//        StatementFilterDTO filter = new StatementFilterDTO();
//        filter.setStartDate(startDate);
//        filter.setEndDate(endDate);
//
//        if (format.equalsIgnoreCase("pdf")) {
//            byte[] pdf = cashFlowStatementExportService.exportToPdf(filter);
//            return buildFileResponse(pdf, "cash-flow", "pdf");
//        } else if (format.equalsIgnoreCase("excel")) {
//            byte[] excel = cashFlowStatementExportService.exportToExcel(filter);
//            return buildFileResponse(excel, "cash-flow", "xlsx");
//        } else {
//            return ResponseEntity.badRequest().body(null);
//        }
//    }
//
//
//    @PostMapping(ApiPath.EQUITY_STATEMENT)
//    @Operation(summary = "Generate Statement of Equity")
//    public ResponseEntity<EquityStatementResponse> generateEquityStatement(@RequestBody StatementFilterDTO filter) {
//        return ResponseEntity.ok(equityStatementService.generateStatement(filter));
//    }
//
//    @GetMapping(ApiPath.EXPORT_EQUITY_STATEMENT)
//    @Operation(summary = "Export Statement of Equity to PDF or Excel")
//    public ResponseEntity<byte[]> exportEquityStatement(
//            @RequestParam String format,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
//    ) {
//        StatementFilterDTO filter = new StatementFilterDTO();
//        filter.setStartDate(startDate);
//        filter.setEndDate(endDate);
//
//        if (format.equalsIgnoreCase("pdf")) {
//            byte[] pdf = equityStatementExportService.exportToPdf(filter);
//            return buildFileResponse(pdf, "equity-statement", "pdf");
//        } else if (format.equalsIgnoreCase("excel")) {
//            byte[] excel = equityStatementExportService.exportToExcel(filter);
//            return buildFileResponse(excel, "equity-statement", "xlsx");
//        } else {
//            return ResponseEntity.badRequest().body(null);
//        }
//    }
//
//
//    @PostMapping(value = ApiPath.VALIDATE_STATEMENT_INTEGRITY, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @Operation(summary = "Validate Statement Integrity Rules (R1–R3)")
//    public ResponseEntity<List<IntegrityCheckResult>> validateStatementIntegrity(@RequestBody BalanceSheetFilterRequest filter) {
//        return ResponseEntity.ok(statementIntegrityService.validateAllStatements(filter));
//    }
//
//
//    @GetMapping(ApiPath.EXPORT_ALL_STATEMENTS)
//    @Operation(summary = "Export All Financial Statements as a ZIP of PDFs or Excel files")
//    public ResponseEntity<byte[]> exportAllStatementsAsZip(
//            @RequestParam String format,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
//    ) {
//        StatementFilterDTO dto = new StatementFilterDTO();
//        dto.setStartDate(startDate);
//        dto.setEndDate(endDate);
//
//        BalanceSheetFilterRequest bsDto = new BalanceSheetFilterRequest();
//        bsDto.setStartDate(startDate);
//        bsDto.setEndDate(endDate);
//
//        ByteArrayOutputStream zipOut = new ByteArrayOutputStream();
//
//        try (java.util.zip.ZipOutputStream zipStream = new java.util.zip.ZipOutputStream(zipOut)) {
//            addToZip(zipStream, "income-statement." + format, format.equals("pdf")
//                    ? incomeStatementExportService.exportToPdf(dto)
//                    : incomeStatementExportService.exportToExcel(dto));
//
//            addToZip(zipStream, "balance-sheet." + format, format.equals("pdf")
//                    ? balanceSheetExportService.exportToPdf(bsDto)
//                    : balanceSheetExportService.exportToExcel(bsDto));
//
//            addToZip(zipStream, "cash-flow." + format, format.equals("pdf")
//                    ? cashFlowStatementExportService.exportToPdf(dto)
//                    : cashFlowStatementExportService.exportToExcel(dto));
//
//            addToZip(zipStream, "equity-statement." + format, format.equals("pdf")
//                    ? equityStatementExportService.exportToPdf(dto)
//                    : equityStatementExportService.exportToExcel(dto));
//
//            zipStream.finish();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to generate ZIP export", e);
//        }
//
//        String filename = "financial-statements_" + LocalDate.now() + ".zip";
//
//        return ResponseEntity.ok()
//                .header("Content-Disposition", "attachment; filename=" + filename)
//                .header("Content-Type", "application/zip")
//                .body(zipOut.toByteArray());
//    }
//
//    private void addToZip(java.util.zip.ZipOutputStream zip, String entryName, byte[] fileContent) throws Exception {
//        java.util.zip.ZipEntry entry = new java.util.zip.ZipEntry(entryName);
//        zip.putNextEntry(entry);
//        zip.write(fileContent);
//        zip.closeEntry();
//    }
//
//}
