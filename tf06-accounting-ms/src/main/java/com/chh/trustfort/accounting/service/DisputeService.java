package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.CreateDisputeRequest;
import com.chh.trustfort.accounting.dto.DisputeResponse;

import java.util.List;

public interface DisputeService {
    DisputeResponse raiseDispute(CreateDisputeRequest request);
    DisputeResponse resolveDispute(String reference, String resolution, String resolvedBy);
    List<DisputeResponse> getAllDisputes();
}
