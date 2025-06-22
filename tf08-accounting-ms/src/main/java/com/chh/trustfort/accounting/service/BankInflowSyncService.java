package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.BankInflowPayload;

public interface BankInflowSyncService {
    String syncInflow(BankInflowPayload payload);
}
