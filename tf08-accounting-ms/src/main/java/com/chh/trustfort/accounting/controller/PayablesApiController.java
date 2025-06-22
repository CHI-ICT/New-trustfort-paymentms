//package com.chh.trustfort.accounting.controller;
//
//import com.chh.trustfort.accounting.constant.ApiPath;
//import com.chh.trustfort.accounting.dto.*;
//import com.chh.trustfort.accounting.service.*;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//@RequestMapping(ApiPath.BASE_API + "/payables")
//@SecurityRequirement(name = "bearerAuth")
//@Tag(name = "Payables APIs", description = "Endpoints for managing payables")
//public class PayablesApiController {
//
//    private final PayableInvoiceService invoiceService;
//    private final ApprovalWorkflowService approvalWorkflowService;
//    private final PaymentSchedulerService paymentScheduleService;
//    private final EOPService eopService;
//    private final PayablesReportService reportService;
//    private final CashflowForecastService forecastService;
//
//    @PostMapping(value = "/invoices", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ApiResponse<?>> submitInvoice(@RequestBody PayableInvoiceRequest request) {
//        log.info("Submitting new invoice for vendor: {}", request.getVendorName());
//        return ResponseEntity.ok(invoiceService.submitInvoice(request));
//    }
//
//    @PostMapping("/approvals/{invoiceId}/action")
//    public ResponseEntity<ApiResponse<?>> approveOrRejectInvoice(@PathVariable Long invoiceId, @RequestParam String action) {
//        log.info("Performing approval action '{}' on invoice ID: {}", action, invoiceId);
//        return ResponseEntity.ok(approvalWorkflowService.processApprovalAction(invoiceId, action));
//    }
//
//    @GetMapping("/schedules/{invoiceId}")
//    public ResponseEntity<ApiResponse<?>> getSchedule(@PathVariable Long invoiceId) {
//        return ResponseEntity.ok(paymentScheduleService.getScheduleByInvoiceId(invoiceId));
//    }
//
//    @GetMapping("/eop/{invoiceId}")
//    public ResponseEntity<ApiResponse<?>> getEOP(@PathVariable Long invoiceId) {
//        return ResponseEntity.ok(eopService.getEOPByInvoiceId(invoiceId));
//    }
//
//    @GetMapping("/reports")
//    public ResponseEntity<ApiResponse<List<PayableInvoiceReportDTO>>> getReports(
//            @RequestParam(required = false) String vendorEmail,
//            @RequestParam(required = false) String status,
//            @RequestParam(required = false) String category
//    ) {
//        return ResponseEntity.ok(reportService.getFilteredReport(vendorEmail, status, category));
//    }
//
//    @PostMapping("/forecast")
//    public ResponseEntity<ApiResponse<List<CashflowForecastResponse>>> forecast(@RequestBody CashflowForecastRequest request) {
//        log.info("Generating forecast from {} to {}", request.getStartDate(), request.getEndDate());
//        List<CashflowForecastResponse> forecast = forecastService.generateForecast(request);
//        return ResponseEntity.ok(ApiResponse.success("Forecast generated successfully", forecast));
//    }
//}
