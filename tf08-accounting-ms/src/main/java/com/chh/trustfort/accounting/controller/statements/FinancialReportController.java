//package com.chh.trustfort.accounting.controller.statements;
//
//import com.chh.trustfort.accounting.Quintuple;
//import com.chh.trustfort.accounting.Util.SecureResponseUtil;
//import com.chh.trustfort.accounting.component.RequestManager;
//import com.chh.trustfort.accounting.component.Role;
//import com.chh.trustfort.accounting.constant.ApiPath;
//import com.chh.trustfort.accounting.dto.*;
//import com.chh.trustfort.accounting.model.AppUser;
//import com.chh.trustfort.accounting.payload.OmniResponsePayload;
//import com.chh.trustfort.accounting.security.AesService;
//import com.chh.trustfort.accounting.service.FinancialReportService;
//import com.google.gson.Gson;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//
//@RestController
//@Slf4j
//@RequiredArgsConstructor
//@Tag(name = "Financial Reports", description = "Endpoints for generating financial reports")
//public class FinancialReportController {
//
//    private final FinancialReportService reportService;
//    private final RequestManager requestManager;
//    private final AesService aesService;
//    private final Gson gson;
//
//    @PostMapping(value = ApiPath.GET_PROFIT_AND_LOSS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> getProfitAndLoss(
//            @RequestParam String idToken,
//            @RequestBody String requestPayload,
//            HttpServletRequest httpRequest) {
//
//        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
//                Role.FINANCIAL_REPORT.getValue(), requestPayload, httpRequest, idToken);
//
//        if (request.isError) {
//            log.warn("❌ Invalid request: {}", request.payload);
//            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
//            return ResponseEntity.status(403).body(aesService.encrypt(SecureResponseUtil.error(
//                    error.getResponseMessage(), error.getResponseCode(), String.valueOf(HttpStatus.BAD_REQUEST)
//            ), null));
//        }
//
//
//        StatementFilterDTO filter = gson.fromJson(request.payload, StatementFilterDTO.class);
//        FinancialReportResponse response = reportService.generateProfitAndLoss(filter.getStartDate(), filter.getEndDate());
//        return ResponseEntity.ok(aesService.encrypt(SecureResponseUtil.success("Profit & Loss generated", response), request.appUser));
//    }
//
//    @PostMapping(value = ApiPath.GET_BALANCE_SHEET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> getBalanceSheet(
//            @RequestParam String idToken,
//            @RequestBody String requestPayload,
//            HttpServletRequest httpRequest) {
//
//        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
//                Role.FINANCIAL_REPORT.getValue(), requestPayload, httpRequest, idToken);
//
//        if (request.isError) {
//            log.warn("❌ Invalid request: {}", request.payload);
//            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
//            return ResponseEntity.status(403).body(aesService.encrypt(SecureResponseUtil.error(
//                    error.getResponseMessage(), error.getResponseCode(), String.valueOf(HttpStatus.BAD_REQUEST)
//            ), null));
//        }
//
//        StatementFilterDTO filter = gson.fromJson(request.payload, StatementFilterDTO.class);
//        BalanceSheetReportDTO response = reportService.generateBalanceSheet(filter.getStartDate());
//        return ResponseEntity.ok(aesService.encrypt(SecureResponseUtil.success("Balance Sheet generated", response), request.appUser));
//    }
//
//    @PostMapping(value = ApiPath.GET_CASH_FLOW, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> getCashFlow(
//            @RequestParam String idToken,
//            @RequestBody String requestPayload,
//            HttpServletRequest httpRequest) {
//
//        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
//                Role.FINANCIAL_REPORT.getValue(), requestPayload, httpRequest, idToken);
//
//        if (request.isError) {
//            log.warn("❌ Invalid request: {}", request.payload);
//            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
//            return ResponseEntity.status(403).body(aesService.encrypt(SecureResponseUtil.error(
//                    error.getResponseMessage(), error.getResponseCode(), String.valueOf(HttpStatus.BAD_REQUEST)
//            ), null));
//        }
//
//        StatementFilterDTO filter = gson.fromJson(request.payload, StatementFilterDTO.class);
//        FinancialReportResponse response = reportService.generateCashFlow(filter.getStartDate(), filter.getEndDate());
//        return ResponseEntity.ok(aesService.encrypt(SecureResponseUtil.success("Cash Flow Report generated", response), request.appUser));
//    }
//
//    @PostMapping(value = ApiPath.GET_CONSOLIDATED_SUMMARY, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> getConsolidatedSummary(
//            @RequestParam String idToken,
//            @RequestBody String requestPayload,
//            HttpServletRequest httpRequest) {
//
//        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
//                Role.FINANCIAL_REPORT.getValue(), requestPayload, httpRequest, idToken);
//
//        if (request.isError) {
//            log.warn("❌ Invalid request: {}", request.payload);
//            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
//            return ResponseEntity.status(403).body(aesService.encrypt(SecureResponseUtil.error(
//                    error.getResponseMessage(), error.getResponseCode(), String.valueOf(HttpStatus.BAD_REQUEST)
//            ), null));
//        }
//
//        StatementFilterDTO filter = gson.fromJson(request.payload, StatementFilterDTO.class);
//        FinancialReportResponse response = reportService.generateConsolidatedSummary(filter.getStartDate(), filter.getEndDate());
//        return ResponseEntity.ok(aesService.encrypt(SecureResponseUtil.success("Consolidated Summary generated", response), request.appUser));
//    }
//}