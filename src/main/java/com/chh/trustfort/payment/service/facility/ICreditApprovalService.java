package com.chh.trustfort.payment.service.facility;

import com.chh.trustfort.payment.dto.ApprovalActionRequest;
import com.chh.trustfort.payment.model.facility.CreditApproval;

import java.util.List;

public interface ICreditApprovalService {

    List<CreditApproval> getPendingApprovals(Long approverId);

    List<CreditApproval> getApprovalsForCreditLine(Long creditLineId);

    void actOnApproval(ApprovalActionRequest request);
}
