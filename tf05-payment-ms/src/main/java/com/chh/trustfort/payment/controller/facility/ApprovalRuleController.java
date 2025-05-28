package com.chh.trustfort.payment.controller.facility;

import com.chh.trustfort.payment.model.facility.ApprovalRule;
import com.chh.trustfort.payment.service.facility.ApprovalRuleAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approval-rules")
public class ApprovalRuleController {

    private final ApprovalRuleAdminService ruleService;

    public ApprovalRuleController(ApprovalRuleAdminService ruleService) {
        this.ruleService = ruleService;
    }

    @GetMapping
    public ResponseEntity<List<ApprovalRule>> getAll() {
        return ResponseEntity.ok(ruleService.getAllRules());
    }

    @PostMapping
    public ResponseEntity<ApprovalRule> create(@RequestBody ApprovalRule rule) {
        return ResponseEntity.ok(ruleService.createRule(rule));
    }

    @PutMapping("/{ruleId}/approver/{newApproverId}")
    public ResponseEntity<ApprovalRule> updateApprover(@PathVariable Long ruleId, @PathVariable Long newApproverId) {
        return ResponseEntity.ok(ruleService.updateApprover(ruleId, newApproverId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        ruleService.softDeleteRule(id);
        return ResponseEntity.ok().build();
    }
}
