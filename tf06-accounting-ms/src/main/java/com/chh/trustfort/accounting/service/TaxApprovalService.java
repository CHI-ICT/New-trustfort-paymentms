package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.enums.ApprovalStatus;
import com.chh.trustfort.accounting.model.TaxApproval;
import com.chh.trustfort.accounting.repository.TaxApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaxApprovalService {

    private final TaxApprovalRepository taxApprovalRepository;

    /**
     * Officer approval step.
     */
    public void approveAsOfficer(Long approvalId, String comment) {
        TaxApproval approval = getApprovalOrThrow(approvalId);

        if (approval.getStatus() != ApprovalStatus.PENDING_OFFICER_APPROVAL) {
            throw new IllegalStateException("Approval not at Officer stage");
        }

        approval.setStatus(ApprovalStatus.PENDING_COMPLIANCE_APPROVAL);
        approval.setComments(comment);
        taxApprovalRepository.save(approval);
    }

    /**
     * Compliance approval step.
     */
    public void approveAsCompliance(Long approvalId, String comment) {
        TaxApproval approval = getApprovalOrThrow(approvalId);

        if (approval.getStatus() != ApprovalStatus.PENDING_COMPLIANCE_APPROVAL) {
            throw new IllegalStateException("Approval not at Compliance stage");
        }

        approval.setStatus(ApprovalStatus.PENDING_EXECUTIVE_APPROVAL);
        approval.setComments(comment);
        taxApprovalRepository.save(approval);
    }

    /**
     * Executive approval step.
     */
    public void approveAsExecutive(Long approvalId, String comment) {
        TaxApproval approval = getApprovalOrThrow(approvalId);

        if (approval.getStatus() != ApprovalStatus.PENDING_EXECUTIVE_APPROVAL) {
            throw new IllegalStateException("Approval not at Executive stage");
        }

        approval.setStatus(ApprovalStatus.APPROVED);
        approval.setComments(comment);
        taxApprovalRepository.save(approval);
    }

    /**
     * Reject the approval.
     */
    public void rejectApproval(Long approvalId, String comment) {
        TaxApproval approval = getApprovalOrThrow(approvalId);
        approval.setStatus(ApprovalStatus.REJECTED);
        approval.setComments(comment);
        taxApprovalRepository.save(approval);
    }

    private TaxApproval getApprovalOrThrow(Long id) {
        return taxApprovalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Approval not found"));
    }
}
