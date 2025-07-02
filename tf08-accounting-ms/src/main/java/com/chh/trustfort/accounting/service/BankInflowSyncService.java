package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.BankInflowPayload;
import com.chh.trustfort.accounting.model.AppUser;

public interface BankInflowSyncService {
    String syncInflow(BankInflowPayload payload, AppUser appUser);
}
