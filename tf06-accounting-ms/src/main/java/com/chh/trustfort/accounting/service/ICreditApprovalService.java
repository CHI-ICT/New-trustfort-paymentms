package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.CreditApproval;
import com.chh.trustfort.accounting.payload.ApprovalActionRequest;

import java.util.List;

public interface ICreditApprovalService {

    List<CreditApproval> getPendingApprovals(Long approverId);

    List<CreditApproval> getApprovalsForCreditLine(Long creditLineId);

    void actOnApproval(ApprovalActionRequest request);
}
