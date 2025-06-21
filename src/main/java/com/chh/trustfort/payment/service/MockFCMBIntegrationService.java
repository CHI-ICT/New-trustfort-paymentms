package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.service.FCMBIntegrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class MockFCMBIntegrationService implements FCMBIntegrationService {

    @Override
    public boolean transferFunds(String settlementAccountNumber, BigDecimal amount) {
        log.info("💸 Mock transfer initiated to FCMB for settlement account [{}] with amount [{}]", settlementAccountNumber, amount);

        // Simulate FCMB network or system logic (random success/failure or always succeed for now)
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            log.info("✅ Mock FCMB transfer SUCCESS for {}", settlementAccountNumber);
            return true; // simulate success
        } else {
            log.error("❌ Mock FCMB transfer FAILED: amount invalid");
            return false;
        }
    }
}
