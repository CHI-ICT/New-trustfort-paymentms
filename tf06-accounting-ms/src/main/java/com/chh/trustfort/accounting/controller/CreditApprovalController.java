package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.model.CreditApproval;
import com.chh.trustfort.accounting.payload.ApprovalActionRequest;
import com.chh.trustfort.accounting.service.CreditApprovalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
public class CreditApprovalController {

    private final CreditApprovalService approvalService;

    public CreditApprovalController(CreditApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    /**
     * Get all pending approvals assigned to an approver
     */
    @GetMapping("/pending")
    public ResponseEntity<List<CreditApproval>> getPendingApprovals(@RequestParam Long approverId) {
        return ResponseEntity.ok(approvalService.getPendingApprovals(approverId));
    }

    /**
     * Act on an approval (approve or reject)
     */
    @PostMapping("/act")
    public ResponseEntity<String> actOnApproval(@RequestBody ApprovalActionRequest request) {
        approvalService.actOnApproval(request);
        return ResponseEntity.ok("Approval action recorded.");
    }

    /**
     * Get approval history for a credit line
     */
    @GetMapping("/credit-line/{creditLineId}")
    public ResponseEntity<List<CreditApproval>> getApprovalHistory(@PathVariable Long creditLineId) {
        return ResponseEntity.ok(approvalService.getApprovalsForCreditLine(creditLineId));
    }
}

