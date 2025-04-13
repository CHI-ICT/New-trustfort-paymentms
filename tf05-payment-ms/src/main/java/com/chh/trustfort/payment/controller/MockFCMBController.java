package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.constant.ApiPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(ApiPath.BASE_API + ApiPath.MOCK_FCMB_BASE)
public class MockFCMBController {

    @PostMapping(ApiPath.SIMULATE_TRANSFER_STATUS)
    public ResponseEntity<String> simulateTransferStatus(@RequestParam String referenceId, @RequestParam boolean success) {
        // You can use this to simulate transfer success/failure from FCMB side
        log.info("📡 Simulated callback received from FCMB: reference={}, success={}", referenceId, success);
        return ResponseEntity.ok("Webhook received");
    }
}
