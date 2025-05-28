package com.chh.trustfort.payment.service.facility;

import com.chh.trustfort.payment.model.facility.ApprovalRule;

import java.util.List;

public interface ApprovalRuleAdminService {

    List<ApprovalRule> getAllRules();

    ApprovalRule createRule(ApprovalRule rule);

    ApprovalRule updateApprover(Long ruleId, Long newApproverId);

    void softDeleteRule(Long id);
}
