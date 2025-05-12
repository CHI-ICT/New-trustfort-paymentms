package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.BankInflowPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BankInflowSyncServiceImpl implements BankInflowSyncService {

    @Override
    public String syncInflow(BankInflowPayload payload) {
        log.info("💳 Received bank inflow: {}", payload);
        // For now, just simulate and log it. Future steps will store this.
        return "Bank inflow synced for reference: " + payload.getReference();
    }
}
