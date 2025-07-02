package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.Responses.ReportApprovalResponse;
import com.chh.trustfort.accounting.dto.ReportApprovalRequest;
import com.chh.trustfort.accounting.enums.ApprovalLevel;
import com.chh.trustfort.accounting.enums.ApprovalStatus;
import com.chh.trustfort.accounting.enums.ReportType;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.ReportApproval;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.repository.ReportApprovalRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.ReportApprovalService;
import com.google.gson.Gson;
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
    private final AesService aesService;

    @Override
    public String initiateApproval(ReportApprovalRequest request, AppUser user) {
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

        OmniResponsePayload response = new OmniResponsePayload();
        response.setResponseCode("00");
        response.setResponseMessage("Approval initiated");
        response.setData(mapToResponse(approval));

        return aesService.encrypt(new Gson().toJson(response), user);
    }

    @Override
    public String approveReport(Long approvalId, String approverEmail, String remarks, AppUser user) {
        ReportApproval approval = reportApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approval record not found"));

        approval.setApprovalStatus(ApprovalStatus.APPROVED);
        approval.setApproverEmail(approverEmail);
        approval.setRemarks(remarks);
        approval.setApprovedAt(LocalDateTime.now());
        approval.setReportStatus("COMPLETED");

        reportApprovalRepository.save(approval);

        OmniResponsePayload response = new OmniResponsePayload();
        response.setResponseCode("00");
        response.setResponseMessage("Report approved");
        response.setData(mapToResponse(approval));

        return aesService.encrypt(new Gson().toJson(response), user);
    }

    @Override
    public String rejectReport(Long approvalId, String approverEmail, String remarks, AppUser user) {
        ReportApproval approval = reportApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approval record not found"));

        approval.setApprovalStatus(ApprovalStatus.REJECTED);
        approval.setApproverEmail(approverEmail);
        approval.setRemarks(remarks);
        approval.setApprovedAt(LocalDateTime.now());
        approval.setReportStatus("REJECTED");

        reportApprovalRepository.save(approval);

        OmniResponsePayload response = new OmniResponsePayload();
        response.setResponseCode("00");
        response.setResponseMessage("Report rejected");
        response.setData(mapToResponse(approval));

        return aesService.encrypt(new Gson().toJson(response), user);
    }

    @Override
    public String getApprovalsForReport(String reportId, AppUser user) {
        List<ReportApproval> approvals = reportApprovalRepository.findByReportId(Long.parseLong(reportId));
        List<ReportApprovalResponse> responseList = approvals.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        OmniResponsePayload response = new OmniResponsePayload();
        response.setResponseCode("00");
        response.setResponseMessage("Approvals fetched");
        response.setData(responseList);

        return aesService.encrypt(new Gson().toJson(response), user);
    }

    @Override
    public String getApprovalById(Long approvalId, AppUser user) {
        ReportApproval approval = reportApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approval record not found"));

        OmniResponsePayload response = new OmniResponsePayload();
        response.setResponseCode("00");
        response.setResponseMessage("Approval found");
        response.setData(mapToResponse(approval));

        return aesService.encrypt(new Gson().toJson(response), user);
    }

    @Override
    public String getApprovalsByStatus(ApprovalStatus status, AppUser user) {
        List<ReportApproval> approvals = reportApprovalRepository.findByApprovalStatus(status);
        List<ReportApprovalResponse> responseList = approvals.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        OmniResponsePayload response = new OmniResponsePayload();
        response.setResponseCode("00");
        response.setResponseMessage("Approvals by status fetched");
        response.setData(responseList);

        return aesService.encrypt(new Gson().toJson(response), user);
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
