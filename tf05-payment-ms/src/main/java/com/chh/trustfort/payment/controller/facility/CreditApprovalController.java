package com.chh.trustfort.payment.controller.facility;

import com.chh.trustfort.payment.dto.ApprovalActionRequest;
import com.chh.trustfort.payment.model.facility.CreditApproval;
import com.chh.trustfort.payment.service.facility.ICreditApprovalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-approvals")
public class CreditApprovalController {

    private final ICreditApprovalService approvalService;

    public CreditApprovalController(ICreditApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @PostMapping("/act")
    public ResponseEntity<Void> actOnApproval(@RequestBody ApprovalActionRequest request) {
        approvalService.actOnApproval(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pending/{approverId}")
    public ResponseEntity<List<CreditApproval>> getPending(@PathVariable Long approverId) {
        return ResponseEntity.ok(approvalService.getPendingApprovals(approverId));
    }
}

