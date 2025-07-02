package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.ApprovalWorkflow;
import com.chh.trustfort.accounting.payload.ApproveInvoiceRequestPayload;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.ApprovalWorkflowService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Approval Workflow", description = "Handles approval of payable invoices")
public class ApprovalWorkflowController {

    private final ApprovalWorkflowService approvalWorkflowService;
    private final AesService aesService;
    private final Gson gson;
    private final RequestManager requestManager;

    @PostMapping(value = ApiPath.APPROVE_INVOICE, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> approveInvoice(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        String idToken = authorizationHeader.replace("Bearer ", "").trim();
        log.info("🔐 ID TOKEN: {}", idToken);
        log.info("📥 RAW PAYLOAD (Base64): {}", requestPayload);

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.APPROVE_INVOICE.getValue(), requestPayload, httpRequest, idToken
        );
        request.appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            String decryptedError = aesService.decrypt(request.payload, request.appUser);
            OmniResponsePayload response = gson.fromJson(decryptedError, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        log.info("📥 Decrypted Payload: {}", request.payload);
        ApproveInvoiceRequestPayload decrypted = gson.fromJson(request.payload, ApproveInvoiceRequestPayload.class);
        String result = approvalWorkflowService.approveInvoice(decrypted, request.appUser);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
