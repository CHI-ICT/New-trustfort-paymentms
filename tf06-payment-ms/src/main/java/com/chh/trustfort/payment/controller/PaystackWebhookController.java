package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Util.SecureResponseUtil;
import com.chh.trustfort.payment.component.AsyncWebhookProcessor;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.component.Role;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.PaystackWebhookPayload;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.payload.OmniResponsePayload;
import com.chh.trustfort.payment.security.AesService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(ApiPath.BASE_API)
public class PaystackWebhookController {

    private final AsyncWebhookProcessor asyncWebhookProcessor;
    private final AesService aesService;
    private final RequestManager requestManager;
    private final Gson gson;

    @PostMapping(value = ApiPath.HANDLE_WEBHOOK, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleWebhook(@RequestParam String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.HANDLE_WEBHOOK.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> decryptedPayload = gson.fromJson(request.payload, Map.class);

            log.info("📩 Received Paystack webhook: {}", decryptedPayload);

            // ✅ Fire-and-forget async processing
            asyncWebhookProcessor.processPaystackWebhook(decryptedPayload);

            String result = "Webhook received. Processing will continue in background.";
            return new ResponseEntity<>(aesService.encrypt(result, request.appUser), HttpStatus.OK);
        } catch (Exception e) {
            log.error("❌ Error initiating webhook processing: {}", e.getMessage(), e);
            String errorMsg = "Webhook processing failed";
            return new ResponseEntity<>(aesService.encrypt(errorMsg, request.appUser), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


