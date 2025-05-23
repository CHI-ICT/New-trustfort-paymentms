package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.ApprovalRule;
import com.chh.trustfort.accounting.repository.ApprovalRuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApprovalRuleAdminServiceImpl implements ApprovalRuleAdminService {

    private final ApprovalRuleRepository ruleRepo;

    public ApprovalRuleAdminServiceImpl(ApprovalRuleRepository ruleRepo) {
        this.ruleRepo = ruleRepo;
    }

    @Override
    public List<ApprovalRule> getAllRules() {
        return ruleRepo.findAllActive();
    }

    @Override
    public ApprovalRule createRule(ApprovalRule rule) {
        validateRule(rule);
        return ruleRepo.save(rule);
    }

    @Override
    public ApprovalRule updateApprover(Long ruleId, Long newApproverId) {
        ApprovalRule rule = ruleRepo.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Approval rule not found"));
        rule.setApproverId(newApproverId);
        ruleRepo.save(rule);
        return rule;
    }

    @Override
    public void softDeleteRule(Long id) {
        ApprovalRule rule = ruleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Approval rule not found"));

        rule.setIsDeleted(true);
        ruleRepo.save(rule);
    }

    private void validateRule(ApprovalRule newRule) {
        List<ApprovalRule> existingRules = ruleRepo.findAllActive();

        for (ApprovalRule rule : existingRules) {
            boolean overlaps = newRule.getMinAmount().compareTo(rule.getMaxAmount()) <= 0 &&
                    newRule.getMaxAmount().compareTo(rule.getMinAmount()) >= 0 &&
                    newRule.getLevel().equals(rule.getLevel());

            if (overlaps) {
                throw new IllegalArgumentException("A rule already exists for this amount range and level");
            }
        }
    }
}

