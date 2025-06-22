package com.chh.trustfort.payment.service.facility;

import com.chh.trustfort.payment.model.facility.CreditLine;

public interface IRuleEngineService {
    void applyApprovalRules(CreditLine creditLine);
}

