package com.chh.trustfort.payment.controller.facility;

import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.model.facility.ApprovalRule;
import com.chh.trustfort.payment.service.facility.ApprovalRuleAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPath.BASE_API)
public class ApprovalRuleController {

    @Autowired
    private ApprovalRuleAdminService ruleService;

    @GetMapping(value = ApiPath.GET_ALL_APPROVAL_RULES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ApprovalRule>> getAll() {
        return ResponseEntity.ok(ruleService.getAllRules());
    }

    @PostMapping(value = ApiPath.CREATE_APPROVAL_RULE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApprovalRule> create(@RequestBody ApprovalRule rule) {
        return ResponseEntity.ok(ruleService.createRule(rule));
    }

    @PutMapping(value = ApiPath.UPDATE_APPROVER, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApprovalRule> updateApprover(
            @PathVariable Long ruleId,
            @PathVariable Long newApproverId
    ) {
        return ResponseEntity.ok(ruleService.updateApprover(ruleId, newApproverId));
    }

    @DeleteMapping(ApiPath.DELETE_APPROVAL_RULE)
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        ruleService.softDeleteRule(id);
        return ResponseEntity.ok().build();
    }
}
