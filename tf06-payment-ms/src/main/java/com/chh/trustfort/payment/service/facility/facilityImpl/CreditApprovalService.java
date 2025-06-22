package com.chh.trustfort.payment.service.facility.facilityImpl;

import com.chh.trustfort.payment.dto.ApprovalActionRequest;
import com.chh.trustfort.payment.enums.CreditApprovalStatus;
import com.chh.trustfort.payment.enums.CreditStatus;
import com.chh.trustfort.payment.exception.BadRequestException;
import com.chh.trustfort.payment.model.facility.ApprovalAuditLog;
import com.chh.trustfort.payment.model.facility.CreditApproval;
import com.chh.trustfort.payment.model.facility.CreditLine;
import com.chh.trustfort.payment.repository.ApprovalAuditLogRepository;
import com.chh.trustfort.payment.repository.CreditApprovalRepository;
import com.chh.trustfort.payment.repository.CreditLineRepository;
import com.chh.trustfort.payment.service.facility.ICreditApprovalService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CreditApprovalService implements ICreditApprovalService {

    private final CreditApprovalRepository approvalRepo;
    private final CreditLineRepository creditLineRepo;
    private final ApprovalAuditLogRepository auditLogRepo;

    public CreditApprovalService(CreditApprovalRepository approvalRepo,
                                 CreditLineRepository creditLineRepo,
                                 ApprovalAuditLogRepository auditLogRepo) {
        this.approvalRepo = approvalRepo;
        this.creditLineRepo = creditLineRepo;
        this.auditLogRepo = auditLogRepo;
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
        validateApprovalRequest(request);

        CreditApproval approval = approvalRepo.findById(request.getApprovalId())
                .orElseThrow(() -> new BadRequestException("Approval with ID " + request.getApprovalId() + " not found"));

        if (!approval.getApproverId().equals(request.getApproverId())) {
            throw new BadRequestException("Approver ID mismatch. Unauthorized action.");
        }

        if (approval.getStatus() != CreditApprovalStatus.PENDING) {
            throw new BadRequestException("This approval has already been acted on.");
        }

        approval.setStatus(request.isApproved() ? CreditApprovalStatus.APPROVED : CreditApprovalStatus.REJECTED);
        approval.setComment(request.getComment());
        approval.setDecisionDate(LocalDateTime.now());
        approvalRepo.save(approval);

        ApprovalAuditLog log = new ApprovalAuditLog();
        log.setApprovalId(approval.getId());
        log.setApproverId(request.getApproverId());
        log.setAction(request.isApproved() ? "APPROVED" : "REJECTED");
        log.setComment(request.getComment());
        log.setCreditLineId(approval.getCreditLineId());
        log.setActionTime(LocalDateTime.now());
        auditLogRepo.save(log);

        finalizeCreditLineIfComplete(approval.getCreditLineId(), request.isApproved());
    }

    private void finalizeCreditLineIfComplete(Long creditLineId, boolean lastDecisionApproved) {
        CreditLine creditLine = creditLineRepo.findById(creditLineId)
                .orElseThrow(() -> new BadRequestException("Credit line with ID " + creditLineId + " not found"));

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

//            disbursementService.disburseIfFullyApproved(creditLineId);
        }
    }

    private void validateApprovalRequest(ApprovalActionRequest request) {
        if (request.getApprovalId() == null) {
            throw new BadRequestException("Approval ID must not be null");
        }
        if (request.getApproverId() == null) {
            throw new BadRequestException("Approver ID must not be null");
        }
        if (request.getComment() == null || request.getComment().trim().isEmpty()) {
            throw new BadRequestException("Comment must not be empty");
        }
    }
}
