package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.ReceiptAlertService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Receipt Alerts", description = "Alert for Pending Receipts")
public class ReceiptAlertController {

    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;
    private final ReceiptAlertService receiptAlertService;

    @GetMapping(value = ApiPath.ALERT_PENDING_RECEIPTS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPendingReceipts(
            @RequestParam String idToken,
            @RequestParam String requestPayload,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.RECEIPT_ALERT.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            log.warn("❌ Authorization failed: {}", request.payload);
            return ResponseEntity.status(401).body(
                    aesService.encrypt(request.payload, request.appUser)
            );
        }

        String encryptedResponse = receiptAlertService.getPendingReceipts(request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }
}
