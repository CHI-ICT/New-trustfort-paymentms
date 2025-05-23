package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.enums.CreditApprovalStatus;
import com.chh.trustfort.accounting.enums.CreditStatus;
import com.chh.trustfort.accounting.model.CreditApproval;
import com.chh.trustfort.accounting.model.CreditLine;
import com.chh.trustfort.accounting.payload.ApprovalActionRequest;
import com.chh.trustfort.accounting.repository.CreditApprovalRepository;
import com.chh.trustfort.accounting.repository.CreditLineRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CreditApprovalService implements ICreditApprovalService {

    private final CreditApprovalRepository approvalRepo;
    private final CreditLineRepository creditLineRepo;
    private final DisbursementService disbursementService;

    public CreditApprovalService(CreditApprovalRepository approvalRepo,
                                 CreditLineRepository creditLineRepo,
                                 DisbursementService disbursementService) {
        this.approvalRepo = approvalRepo;
        this.creditLineRepo = creditLineRepo;
        this.disbursementService = disbursementService;
    }

    @Override
    public List<CreditApproval> getPendingApprovals(Long approverId) {
        return approvalRepo.findByApproverIdAndStatus(approverId, CreditApprovalStatus.PENDING);
    }

    @Override
    public List<CreditApproval> getApprovalsForCreditLine(Long creditLineId) {
        return approvalRepo.findByCreditLineId(creditLineId);
    }

    @Override
    @Transactional
    public void actOnApproval(ApprovalActionRequest request) {
        CreditApproval approval = approvalRepo.findById(request.getApprovalId())
                .orElseThrow(() -> new RuntimeException("Approval not found"));

        if (!approval.getApproverId().equals(request.getApproverId())) {
            throw new RuntimeException("Unauthorized action.");
        }

        if (approval.getStatus() != CreditApprovalStatus.PENDING) {
            throw new RuntimeException("Approval already completed.");
        }

        approval.setStatus(request.isApproved() ? CreditApprovalStatus.APPROVED : CreditApprovalStatus.REJECTED);
        approval.setComment(request.getComment());
        approval.setDecisionDate(LocalDateTime.now());
        approvalRepo.save(approval);

        finalizeCreditLineIfComplete(approval.getCreditLineId(), request.isApproved());
    }

    private void finalizeCreditLineIfComplete(Long creditLineId, boolean lastDecisionApproved) {
        CreditLine creditLine = creditLineRepo.findById(creditLineId)
                .orElseThrow(() -> new RuntimeException("Credit line not found"));

        if (!lastDecisionApproved) {
            creditLine.setStatus(CreditStatus.REJECTED);
            creditLineRepo.save(creditLine);
            return;
        }

        boolean allApproved = approvalRepo.findByCreditLineId(creditLineId).stream()
                .allMatch(a -> a.getStatus() == CreditApprovalStatus.APPROVED);

        if (allApproved) {
            creditLine.setStatus(CreditStatus.APPROVED);
            creditLine.setApprovedDate(LocalDateTime.now());
            creditLineRepo.save(creditLine);

            disbursementService.disburseIfFullyApproved(creditLineId);
        }
    }
}

