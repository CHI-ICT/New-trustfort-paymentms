package com.chh.trustfort.payment.service.facility.facilityImpl;
import com.chh.trustfort.payment.model.facility.ApprovalRule;
import com.chh.trustfort.payment.repository.ApprovalRuleRepository;
import com.chh.trustfort.payment.service.facility.ApprovalRuleAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApprovalRuleAdminServiceImpl implements ApprovalRuleAdminService {

    private final ApprovalRuleRepository ruleRepository;

    @Autowired
    public ApprovalRuleAdminServiceImpl(ApprovalRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @Override
    public List<ApprovalRule> getAllRules() {
        return ruleRepository.findAllByIsDeletedFalse();
    }

    @Override
    public ApprovalRule createRule(ApprovalRule rule) {
        rule.setCreatedAt(LocalDateTime.now());
        rule.setIsDeleted(false);
        return ruleRepository.save(rule);
    }

    @Override
    public ApprovalRule updateApprover(Long ruleId, Long newApproverId) {
        ApprovalRule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Approval rule not found"));
        rule.setApproverId(newApproverId);
        rule.setUpdatedAt(LocalDateTime.now());
        return ruleRepository.save(rule);
    }

    @Override
    public void softDeleteRule(Long id) {
        ApprovalRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Approval rule not found"));
        rule.setIsDeleted(true);
        rule.setUpdatedAt(LocalDateTime.now());
        ruleRepository.save(rule);
    }
}

