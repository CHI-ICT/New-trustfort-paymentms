package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.component.AsyncWebhookProcessor;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.PaystackWebhookPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
@Slf4j
public class PaystackWebhookController {

    private final AsyncWebhookProcessor asyncWebhookProcessor;

    @PostMapping(value = ApiPath.HANDLE_WEBHOOK, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        try {
            log.info("📩 Received Paystack webhook: {}", payload);

            // ✅ Fire-and-forget async processing
            asyncWebhookProcessor.processPaystackWebhook((Map<String, Object>) payload);

            // ✅ Immediately acknowledge Paystack
            return ResponseEntity.ok("Webhook received. Processing will continue in background.");
        } catch (Exception e) {
            log.error("❌ Error initiating webhook processing", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook processing failed");
        }
    }
}

