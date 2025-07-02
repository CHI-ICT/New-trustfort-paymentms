package com.chh.trustfort.accounting.controller.statements;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ApiResponse;
import com.chh.trustfort.accounting.dto.CashFlowStatementDTO;
import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.CashFlowStatementService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequiredArgsConstructor
@Tag(name = "Cash Flow Statement", description = "Generates cash flow statement based on GL activities")
@Slf4j
public class CashFlowStatementController {

    private final CashFlowStatementService cashFlowStatementService;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;

    @PostMapping(value = ApiPath.CASH_FLOW_STATEMENT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCashFlowStatement(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        log.info("📊 Received request to generate cash flow statement");

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.CASH_FLOW_STATEMENT.getValue(), requestPayload, httpRequest, idToken
        );

        AppUser appUser = request.appUser;
        appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.ok(
                    aesService.encrypt(
                            SecureResponseUtil.error(
                                    error.getResponseCode(), error.getResponseMessage(), String.valueOf(HttpStatus.UNAUTHORIZED)
                            ), appUser
                    )
            );
        }

        StatementFilterDTO filter = gson.fromJson(request.payload, StatementFilterDTO.class);
        CashFlowStatementDTO result = cashFlowStatementService.generateCashFlowStatement(filter);
        ApiResponse response = ApiResponse.error(ApiResponse.success("Cash flow statement generated successfully", result));

        return ResponseEntity.ok(aesService.encrypt(String.valueOf(response), appUser));
    }
}


