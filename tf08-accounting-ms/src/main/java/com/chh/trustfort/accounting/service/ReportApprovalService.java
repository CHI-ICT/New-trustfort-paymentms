package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.Responses.GenericApiResponse;
import com.chh.trustfort.accounting.Responses.ReportApprovalResponse;
import com.chh.trustfort.accounting.dto.ReportApprovalRequest;
import com.chh.trustfort.accounting.enums.ApprovalStatus;
import com.chh.trustfort.accounting.model.AppUser;

import java.util.List;

public interface ReportApprovalService {

    String initiateApproval(ReportApprovalRequest request, AppUser user);

    String approveReport(Long approvalId, String approverEmail, String remarks, AppUser user);

    String rejectReport(Long approvalId, String approverEmail, String remarks, AppUser user);

    String getApprovalsForReport(String reportId, AppUser user);

    String getApprovalById(Long approvalId, AppUser user);

    String getApprovalsByStatus(ApprovalStatus status, AppUser user);
}

