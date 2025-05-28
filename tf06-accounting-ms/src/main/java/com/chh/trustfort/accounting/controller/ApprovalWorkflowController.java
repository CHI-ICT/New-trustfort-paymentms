package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.model.ApprovalWorkflow;
import com.chh.trustfort.accounting.service.ApprovalWorkflowService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@EncryptResponse
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.BASE_API)
@Tag(name = "Approval Workflow", description = "Handles approval of payable invoices")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class ApprovalWorkflowController {

    private final ApprovalWorkflowService approvalWorkflowService;


    @PostMapping(value = ApiPath.APPROVE_INVOICE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> approveInvoice(
            @PathVariable Long invoiceId,
            @RequestParam String approverEmail,
            @RequestParam String approverRole,
            @RequestParam(required = false) String comment) {

        try {
            ApprovalWorkflow workflow = approvalWorkflowService.approveInvoice(invoiceId, approverEmail, approverRole, comment);
            return ResponseEntity.status(HttpStatus.CREATED).body(workflow);
        } catch (Exception e) {
            log.error("Approval failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Approval failed: " + e.getMessage());
        }
    }
}
