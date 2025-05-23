package com.chh.trustfort.accounting.service;

public interface IDisbursementService {
    void disburseIfFullyApproved(Long creditLineId);
}

