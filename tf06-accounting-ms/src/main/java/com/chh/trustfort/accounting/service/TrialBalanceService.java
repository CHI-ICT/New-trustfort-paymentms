package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.payload.TrialBalanceRequest;
import com.chh.trustfort.accounting.payload.TrialBalanceResponse;

public interface TrialBalanceService {
    TrialBalanceResponse generateTrialBalance(TrialBalanceRequest request);
}
