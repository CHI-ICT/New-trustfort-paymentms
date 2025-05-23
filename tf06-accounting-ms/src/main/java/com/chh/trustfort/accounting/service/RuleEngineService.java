package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.enums.CreditApprovalStatus;
import com.chh.trustfort.accounting.model.ApprovalRule;
import com.chh.trustfort.accounting.model.CreditApproval;
import com.chh.trustfort.accounting.model.CreditLine;
import com.chh.trustfort.accounting.repository.ApprovalRuleRepository;
import com.chh.trustfort.accounting.repository.CreditApprovalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RuleEngineService implements IRuleEngineService {

    private final ApprovalRuleRepository ruleRepo;
    private final CreditApprovalRepository approvalRepo;

    public RuleEngineService(ApprovalRuleRepository ruleRepo,
                             CreditApprovalRepository approvalRepo) {
        this.ruleRepo = ruleRepo;
        this.approvalRepo = approvalRepo;
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

            CreditApproval approval = new CreditApproval();
            approval.setCreditLineId(creditLine.getId());
            approval.setLevel(rule.getLevel());
            approval.setApproverId(rule.getApproverId());
            approval.setStatus(CreditApprovalStatus.PENDING);
            approval.setCreatedAt(LocalDateTime.now());

            approvalRepo.save(approval);
        }
    }
}
