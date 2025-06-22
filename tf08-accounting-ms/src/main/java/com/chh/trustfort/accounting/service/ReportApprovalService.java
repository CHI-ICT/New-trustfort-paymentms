package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.Responses.GenericApiResponse;
import com.chh.trustfort.accounting.Responses.ReportApprovalResponse;
import com.chh.trustfort.accounting.dto.ReportApprovalRequest;
import com.chh.trustfort.accounting.enums.ApprovalStatus;

import java.util.List;

public interface ReportApprovalService {

    GenericApiResponse<ReportApprovalResponse> initiateApproval(ReportApprovalRequest request);

    GenericApiResponse<ReportApprovalResponse> approveReport(Long approvalId, String approverEmail, String remarks);

    GenericApiResponse<ReportApprovalResponse> rejectReport(Long approvalId, String approverEmail, String remarks);

    GenericApiResponse<List<ReportApprovalResponse>> getApprovalsForReport(String reportId);

    GenericApiResponse<ReportApprovalResponse> getApprovalById(Long approvalId);

    GenericApiResponse<List<ReportApprovalResponse>> getApprovalsByStatus(ApprovalStatus status);
}

