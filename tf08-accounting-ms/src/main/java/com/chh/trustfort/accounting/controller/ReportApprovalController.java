package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Responses.GenericApiResponse;
import com.chh.trustfort.accounting.Responses.ReportApprovalResponse;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ReportApprovalRequest;
import com.chh.trustfort.accounting.enums.ApprovalStatus;
import com.chh.trustfort.accounting.service.ReportApprovalService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
@Tag(name = "Report Approval", description = "Endpoints for report approval workflow")
@SecurityRequirement(name = "bearerAuth")
public class ReportApprovalController {

    private final ReportApprovalService reportApprovalService;

    @PostMapping(value = ApiPath.INITIATE_REPORT_APPROVAL, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericApiResponse<ReportApprovalResponse>> initiateApproval(
            @RequestBody ReportApprovalRequest request) {
        log.info("Initiating approval for report: {}", request.getReportId());
        return ResponseEntity.ok(reportApprovalService.initiateApproval(request));
    }

    @PostMapping(value = ApiPath.APPROVE_REPORT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericApiResponse<ReportApprovalResponse>> approveReport(
            @RequestParam Long approvalId,
            @RequestParam String approverEmail,
            @RequestParam String remarks) {
        log.info("Approving report approval ID: {}", approvalId);
        return ResponseEntity.ok(reportApprovalService.approveReport(approvalId, approverEmail, remarks));
    }

    @PostMapping(value = ApiPath.REJECT_REPORT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericApiResponse<ReportApprovalResponse>> rejectReport(
            @RequestParam Long approvalId,
            @RequestParam String approverEmail,
            @RequestParam String remarks) {
        log.info("Rejecting report approval ID: {}", approvalId);
        return ResponseEntity.ok(reportApprovalService.rejectReport(approvalId, approverEmail, remarks));
    }

    @GetMapping(value = ApiPath.GET_APPROVALS_FOR_REPORT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericApiResponse<List<ReportApprovalResponse>>> getApprovalsForReport(
            @RequestParam String reportId) {
        log.info("Fetching approvals for report ID: {}", reportId);
        return ResponseEntity.ok(reportApprovalService.getApprovalsForReport(reportId));
    }

    @GetMapping(value = ApiPath.GET_APPROVAL_BY_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericApiResponse<ReportApprovalResponse>> getApprovalById(
            @RequestParam Long approvalId) {
        log.info("Fetching approval by ID: {}", approvalId);
        return ResponseEntity.ok(reportApprovalService.getApprovalById(approvalId));
    }

    @GetMapping(value = ApiPath.GET_APPROVALS_BY_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericApiResponse<List<ReportApprovalResponse>>> getApprovalsByStatus(
            @RequestParam ApprovalStatus status) {
        log.info("Fetching approvals with status: {}", status);
        return ResponseEntity.ok(reportApprovalService.getApprovalsByStatus(status));
    }
}
