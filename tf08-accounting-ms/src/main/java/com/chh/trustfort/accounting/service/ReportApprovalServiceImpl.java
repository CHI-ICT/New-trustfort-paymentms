package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.Responses.GenericApiResponse;
import com.chh.trustfort.accounting.Responses.ReportApprovalResponse;
import com.chh.trustfort.accounting.dto.ReportApprovalRequest;
import com.chh.trustfort.accounting.enums.ApprovalLevel;
import com.chh.trustfort.accounting.enums.ApprovalStatus;
import com.chh.trustfort.accounting.enums.ReportType;
import com.chh.trustfort.accounting.model.ReportApproval;
import com.chh.trustfort.accounting.repository.ReportApprovalRepository;
import com.chh.trustfort.accounting.service.ReportApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportApprovalServiceImpl implements ReportApprovalService {

    private final ReportApprovalRepository reportApprovalRepository;

    @Override
    public GenericApiResponse<ReportApprovalResponse> initiateApproval(ReportApprovalRequest request) {
        ReportApproval approval = ReportApproval.builder()
                .reportId(request.getReportId())
                .reportType(ReportType.valueOf(request.getReportType()))
                .approverEmail(request.getApproverEmail())
                .approvalStatus(ApprovalStatus.valueOf(request.getApprovalStatus()))
                .remarks(request.getRemarks())
                .approvedAt(LocalDateTime.now())
                .approvalLevel(ApprovalLevel.L1)
                .reportStatus("PENDING")
                .build();

        reportApprovalRepository.save(approval);
        return GenericApiResponse.success("Approval initiated", mapToResponse(approval));
    }

    @Override
    public GenericApiResponse<ReportApprovalResponse> approveReport(Long approvalId, String approverEmail, String remarks) {
        ReportApproval approval = reportApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approval record not found"));

        approval.setApprovalStatus(ApprovalStatus.APPROVED);
        approval.setApproverEmail(approverEmail);
        approval.setRemarks(remarks);
        approval.setApprovedAt(LocalDateTime.now());
        approval.setReportStatus("COMPLETED");

        reportApprovalRepository.save(approval);
        return GenericApiResponse.success("Report approved", mapToResponse(approval));
    }

    @Override
    public GenericApiResponse<ReportApprovalResponse> rejectReport(Long approvalId, String approverEmail, String remarks) {
        ReportApproval approval = reportApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approval record not found"));

        approval.setApprovalStatus(ApprovalStatus.REJECTED);
        approval.setApproverEmail(approverEmail);
        approval.setRemarks(remarks);
        approval.setApprovedAt(LocalDateTime.now());
        approval.setReportStatus("REJECTED");

        reportApprovalRepository.save(approval);
        return GenericApiResponse.success("Report rejected", mapToResponse(approval));
    }

    @Override
    public GenericApiResponse<List<ReportApprovalResponse>> getApprovalsForReport(String reportId) {
        List<ReportApproval> approvals = reportApprovalRepository.findByReportId(Long.parseLong(reportId));
        List<ReportApprovalResponse> responseList = approvals.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return GenericApiResponse.success("Approvals fetched", responseList);
    }

    @Override
    public GenericApiResponse<ReportApprovalResponse> getApprovalById(Long approvalId) {
        ReportApproval approval = reportApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approval record not found"));
        return GenericApiResponse.success("Approval found", mapToResponse(approval));
    }

    @Override
    public GenericApiResponse<List<ReportApprovalResponse>> getApprovalsByStatus(ApprovalStatus status) {
        List<ReportApproval> approvals = reportApprovalRepository.findByApprovalStatus(status);
        List<ReportApprovalResponse> responseList = approvals.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return GenericApiResponse.success("Approvals by status fetched", responseList);
    }

    private ReportApprovalResponse mapToResponse(ReportApproval approval) {
        return ReportApprovalResponse.builder()
                .approvalId(approval.getId())
                .reportId(approval.getReportId())
                .reportType(String.valueOf(approval.getReportType()))
                .approverEmail(approval.getApproverEmail())
                .approvalStatus(approval.getApprovalStatus().name())
                .remarks(approval.getRemarks())
                .approvedAt(approval.getApprovedAt().toString())
                .approvalLevel(approval.getApprovalLevel().name())
                .nextApprover(approval.getNextApprover())
                .reportStatus(approval.getReportStatus())
                .build();
    }
}
