package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.model.ApprovalRule;
import com.chh.trustfort.accounting.payload.UpdateApproverRequest;
import com.chh.trustfort.accounting.service.ApprovalRuleAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/approval-rules")
public class ApprovalRuleAdminController {

    private final ApprovalRuleAdminService adminService;

    public ApprovalRuleAdminController(ApprovalRuleAdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public List<ApprovalRule> getAllRules() {
        return adminService.getAllRules();
    }

    @PostMapping
    public ApprovalRule createRule(@RequestBody ApprovalRule rule) {
        return adminService.createRule(rule);
    }

    @PutMapping("/{id}/approver")
    public ResponseEntity<String> updateApprover(
            @PathVariable Long id,
            @RequestBody UpdateApproverRequest request
    ) {
        adminService.updateApprover(id, request.getNewApproverId());
        return ResponseEntity.ok("Approver updated successfully");
    }

    @DeleteMapping("/{id}")
    public void softDeleteRule(@PathVariable Long id) {
        adminService.softDeleteRule(id);
    }
}

