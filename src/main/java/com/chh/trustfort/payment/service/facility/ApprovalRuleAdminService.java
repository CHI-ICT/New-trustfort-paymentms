package com.chh.trustfort.payment.service.facility;

import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.model.facility.ApprovalRule;

import java.util.List;

public interface ApprovalRuleAdminService {

    List<ApprovalRule> getAllRules();

    String createRule(String requestPayload, String idToken, AppUser user);

    ApprovalRule updateApprover(Long ruleId, Long newApproverId);

    void softDeleteRule(Long id);
}
