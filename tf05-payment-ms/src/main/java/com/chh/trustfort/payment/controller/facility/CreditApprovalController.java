package com.chh.trustfort.payment.controller.facility;

import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.ApprovalActionRequest;
import com.chh.trustfort.payment.model.facility.CreditApproval;
import com.chh.trustfort.payment.service.facility.ICreditApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPath.BASE_API)
public class CreditApprovalController {

    @Autowired
    private ICreditApprovalService approvalService;

    @PostMapping(value = ApiPath.ACT_ON_APPROVAL, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> actOnApproval(@RequestBody ApprovalActionRequest request) {
        approvalService.actOnApproval(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = ApiPath.GET_PENDING_APPROVALS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CreditApproval>> getPending(@PathVariable Long approverId) {
        return ResponseEntity.ok(approvalService.getPendingApprovals(approverId));
    }
}
