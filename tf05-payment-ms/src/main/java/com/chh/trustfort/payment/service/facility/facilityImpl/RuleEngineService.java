package com.chh.trustfort.payment.service.facility.facilityImpl;

import com.chh.trustfort.payment.enums.CreditApprovalStatus;
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
                             CreditApprovalRepository approvalRepo, ApprovalAuditLogRepository auditLogRepo) {
        this.ruleRepo = ruleRepo;
        this.approvalRepo = approvalRepo;
        this.auditLogRepo = auditLogRepo;
    }

    @Override
    public void applyApprovalRules(CreditLine creditLine) {
        BigDecimal amount = creditLine.getAmount();

        // Load rules by amount range
        List<ApprovalRule> rules = ruleRepo.findByAmountRange(amount);

        if (rules.isEmpty()) {
            throw new RuntimeException("No approval rules configured for amount: " + amount);
        }

        // Create CreditApproval entries
        for (ApprovalRule rule : rules) {
            if (Boolean.TRUE.equals(rule.getIsDeleted())) continue;

            // Check for duplicate approval entry
            boolean alreadyExists = approvalRepo.existsByCreditLineIdAndApproverId(
                    creditLine.getId(), rule.getApproverId());

            if (alreadyExists) continue;

            CreditApproval approval = new CreditApproval();
            approval.setCreditLineId(creditLine.getId());
            approval.setLevel(rule.getLevel());
            approval.setApproverId(rule.getApproverId());
            approval.setStatus(CreditApprovalStatus.PENDING);
            approval.setCreatedAt(LocalDateTime.now());

            approvalRepo.save(approval);

            // Audit log for creation
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
