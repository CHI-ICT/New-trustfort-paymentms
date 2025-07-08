package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.service.PaystackWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaystackWebhookController {

    private final PaystackWebhookService webhookService;

    @PostMapping(value = ApiPath.PAYSTACK_WEBHOOK, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleWebhook(@RequestBody String payload, HttpServletRequest request) {
        log.info("📨 Received Paystack webhook from IP {}: {}", request.getRemoteAddr(), payload);

        boolean success = webhookService.handleWebhook(payload);

        if (!success) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("status", "fail", "message", "Webhook already handled or malformed"));
        }

        return ResponseEntity.ok(Map.of("status", "success", "message", "Webhook processed successfully"));
    }
}
