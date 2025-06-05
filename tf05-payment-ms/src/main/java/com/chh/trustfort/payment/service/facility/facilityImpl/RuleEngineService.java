package com.chh.trustfort.payment.service.facility.facilityImpl;

import com.chh.trustfort.payment.enums.CreditApprovalStatus;
import com.chh.trustfort.payment.exception.BadRequestException;
import com.chh.trustfort.payment.model.facility.ApprovalAuditLog;
import com.chh.trustfort.payment.model.facility.ApprovalRule;
import com.chh.trustfort.payment.model.facility.CreditApproval;
import com.chh.trustfort.payment.model.facility.CreditLine;
import com.chh.trustfort.payment.repository.ApprovalAuditLogRepository;
import com.chh.trustfort.payment.repository.ApprovalRuleRepository;
import com.chh.trustfort.payment.repository.CreditApprovalRepository;
import com.chh.trustfort.payment.service.facility.IRuleEngineService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RuleEngineService implements IRuleEngineService {

    private final ApprovalRuleRepository ruleRepo;
    private final CreditApprovalRepository approvalRepo;
    private final ApprovalAuditLogRepository auditLogRepo;

    public RuleEngineService(ApprovalRuleRepository ruleRepo,
                             CreditApprovalRepository approvalRepo,
                             ApprovalAuditLogRepository auditLogRepo) {
        this.ruleRepo = ruleRepo;
        this.approvalRepo = approvalRepo;
        this.auditLogRepo = auditLogRepo;
    }

    @Override
    public void applyApprovalRules(CreditLine creditLine) {
        if (creditLine == null || creditLine.getAmount() == null || creditLine.getId() == null) {
            throw new BadRequestException("Invalid credit line: missing ID or amount.");
        }

        BigDecimal amount = creditLine.getAmount();

        List<ApprovalRule> rules = ruleRepo.findByAmountRange(amount);
        if (rules.isEmpty()) {
            throw new BadRequestException("No approval rules configured for credit amount: " + amount);
        }

        for (ApprovalRule rule : rules) {
            if (Boolean.TRUE.equals(rule.getIsDeleted())) continue;

            boolean alreadyExists = approvalRepo.existsByCreditLineIdAndApproverId(
                    creditLine.getId(), rule.getApproverId());

            if (alreadyExists) continue;

            CreditApproval approval = new CreditApproval();
            approval.setCreditLineId(creditLine.getId());
            approval.setLevel(rule.getLevel());
            approval.setApproverId(rule.getApproverId());
            approval.setStatus(CreditApprovalStatus.PENDING);
            approval.setCreatedAt(LocalDateTime.now());

            approval = approvalRepo.save(approval);

            ApprovalAuditLog log = new ApprovalAuditLog();
            log.setApprovalId(approval.getId());
            log.setApproverId(rule.getApproverId());
            log.setAction("CREATED");
            log.setCreditLineId(creditLine.getId());
            log.setActionTime(LocalDateTime.now());
            auditLogRepo.save(log);
        }
    }
}
