package com.chh.trustfort.payment.service.facility.facilityImpl;

import com.chh.trustfort.payment.exception.BadRequestException;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.model.facility.ApprovalRule;
import com.chh.trustfort.payment.repository.ApprovalRuleRepository;
import com.chh.trustfort.payment.service.facility.ApprovalRuleAdminService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApprovalRuleAdminServiceImpl implements ApprovalRuleAdminService {

    private final ApprovalRuleRepository ruleRepository;
    private final Gson gson;
    @Autowired
    public ApprovalRuleAdminServiceImpl(ApprovalRuleRepository ruleRepository, Gson gson) {
        this.ruleRepository = ruleRepository;
        this.gson = gson;
    }

    @Override
    public List<ApprovalRule> getAllRules() {
        return ruleRepository.findAllByIsDeletedFalse();
    }

    @Override
    public String createRule(String requestPayload, String idToken, AppUser user) {
        ApprovalRule rule = gson.fromJson(requestPayload, ApprovalRule.class);

        validateRuleForCreate(rule);
        rule.setCreatedAt(LocalDateTime.now());
        rule.setIsDeleted(false);
        rule.setCreatedBy(user.getUserName());
        ApprovalRule savedRule = ruleRepository.save(rule);

        return gson.toJson(savedRule);
    }

    @Override
    public ApprovalRule updateApprover(Long ruleId, Long newApproverId) {
        if (newApproverId == null) {
            throw new BadRequestException("New approver ID must not be null");
        }

        ApprovalRule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new BadRequestException("Approval rule with ID " + ruleId + " not found"));

        rule.setApproverId(newApproverId);
        rule.setUpdatedAt(LocalDateTime.now());
        return ruleRepository.save(rule);
    }

    @Override
    public void softDeleteRule(Long id) {
        ApprovalRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Approval rule with ID " + id + " not found"));

        rule.setIsDeleted(true);
        rule.setUpdatedAt(LocalDateTime.now());
        ruleRepository.save(rule);
    }
    private void validateRuleForCreate(ApprovalRule rule) {
        if (rule == null) {
            throw new BadRequestException("Approval rule payload must not be null.");
        }

        if (rule.getMinAmount() == null) {
            throw new BadRequestException("Minimum amount is required.");
        }

        if (rule.getMinAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Minimum amount must be non-negative.");
        }

        if (rule.getMaxAmount() == null) {
            throw new BadRequestException("Maximum amount is required.");
        }

        if (rule.getMaxAmount().compareTo(rule.getMinAmount()) <= 0) {
            throw new BadRequestException("Maximum amount must be greater than minimum amount.");
        }

        if (rule.getLevel() == null) {
            throw new BadRequestException("Approval level is required.");
        }

        if (rule.getLevel() < 1) {
            throw new BadRequestException("Approval level must be at least 1.");
        }

        if (rule.getApproverId() == null) {
            throw new BadRequestException("Approver ID must not be null.");
        }

        if (rule.getApproverId() <= 0) {
            throw new BadRequestException("Approver ID must be a positive number.");
        }
        if (rule.getName() == null || rule.getName().trim().isEmpty()) {
            throw new BadRequestException("Rule name is required.");
        }

        if (ruleRepository.existsByName(rule.getName())) {
            throw new BadRequestException("An approval rule with this name already exists.");
        }
    }
}
