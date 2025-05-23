package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.CreditLine;

public interface IRuleEngineService {
    void applyApprovalRules(CreditLine creditLine);
}

