package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.ApprovalRule;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ApprovalRuleAdminService {

    List<ApprovalRule> getAllRules();

    ApprovalRule createRule(ApprovalRule rule);

    ApprovalRule updateApprover(Long ruleId, Long newApproverId);

    void softDeleteRule(Long id);
}
