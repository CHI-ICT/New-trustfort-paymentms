package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.service.FlutterwaveWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FlutterwaveWebhookController {

    private final FlutterwaveWebhookService flutterwaveWebhookService;

    @PostMapping(value = ApiPath.FLUTTERWAVE_WEBHOOK, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleWebhook(@RequestBody String payload, HttpServletRequest request) {
        log.info("📨 Received Flutterwave webhook from IP {}: {}", request.getRemoteAddr(), payload);

        boolean success = flutterwaveWebhookService.handleWebhook(payload);

        if (!success) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("status", "fail", "message", "Duplicate tx_ref or invalid userId"));
        }

        return ResponseEntity.ok(Map.of("status", "success", "message", "Webhook processed"));
    }
    }


