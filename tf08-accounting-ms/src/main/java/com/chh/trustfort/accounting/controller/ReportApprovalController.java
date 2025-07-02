package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Responses.GenericApiResponse;
import com.chh.trustfort.accounting.Responses.ReportApprovalResponse;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ReportApprovalRequest;
import com.chh.trustfort.accounting.enums.ApprovalStatus;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.ReportApprovalService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Report Approval", description = "Endpoints for report approval workflow")
public class ReportApprovalController {

    private final ReportApprovalService reportApprovalService;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;


    @PostMapping(value = ApiPath.INITIATE_REPORT_APPROVAL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> initiateApproval(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.REPORT_APPROVAL.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            log.warn("❌ Decryption failed or unauthorized access");
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(aesService.encrypt(gson.toJson(error), null));
        }

        ReportApprovalRequest approvalRequest = new Gson().fromJson(request.payload, ReportApprovalRequest.class);
        log.info("✅ Decrypted approval request for report ID: {}", approvalRequest.getReportId());

        String encryptedResponse = reportApprovalService.initiateApproval(approvalRequest, request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }

    @PostMapping(value = ApiPath.APPROVE_REPORT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> approveReport(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.REPORT_APPROVAL.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            log.warn("❌ Decryption failed or unauthorized access");
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(aesService.encrypt(gson.toJson(error), null));
        }

        JsonObject json = JsonParser.parseString(request.payload).getAsJsonObject();
        Long approvalId = json.get("approvalId").getAsLong();
        String approverEmail = json.get("approverEmail").getAsString();
        String remarks = json.get("remarks").getAsString();

        log.info("✅ Approving report with ID: {}", approvalId);
        String encryptedResponse = reportApprovalService.approveReport(approvalId, approverEmail, remarks, request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }

    @PostMapping(value = ApiPath.REJECT_REPORT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> rejectReport(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.REPORT_APPROVAL.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            log.warn("❌ Decryption failed or unauthorized access");
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(aesService.encrypt(gson.toJson(error), null));
        }

        JsonObject json = JsonParser.parseString(request.payload).getAsJsonObject();
        Long approvalId = json.get("approvalId").getAsLong();
        String approverEmail = json.get("approverEmail").getAsString();
        String remarks = json.get("remarks").getAsString();

        log.info("🚫 Rejecting report with ID: {}", approvalId);
            String encryptedResponse = reportApprovalService.rejectReport(approvalId, approverEmail, remarks, request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }

    @GetMapping(value = ApiPath.GET_APPROVALS_FOR_REPORT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getApprovalsForReport(
            @RequestParam String reportId,
            @RequestParam String idToken,
            HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.REPORT_APPROVAL.getValue(), reportId, httpRequest, idToken
        );

        if (request.isError) {
            log.warn("❌ Decryption failed or unauthorized access");
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(aesService.encrypt(gson.toJson(error), null));
        }

        log.info("📄 Fetching approvals for report: {}", reportId);
        String encryptedResponse = reportApprovalService.getApprovalsForReport(reportId, request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }

    @GetMapping(value = ApiPath.GET_APPROVAL_BY_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getApprovalById(
            @RequestParam Long approvalId,
            @RequestParam String idToken,
            HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.REPORT_APPROVAL.getValue(), String.valueOf(approvalId), httpRequest, idToken
        );

        if (request.isError) {
            log.warn("❌ Decryption failed or unauthorized access");
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(aesService.encrypt(gson.toJson(error), null));
        }

        log.info("📄 Fetching approval by ID: {}", approvalId);
        String encryptedResponse = reportApprovalService.getApprovalById(approvalId, request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }

    @GetMapping(value = ApiPath.GET_APPROVALS_BY_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getApprovalsByStatus(
            @RequestParam ApprovalStatus status,
            @RequestParam String idToken,
            HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.REPORT_APPROVAL.getValue(), status.name(), httpRequest, idToken
        );

        if (request.isError) {
            log.warn("❌ Decryption failed or unauthorized access");
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(aesService.encrypt(gson.toJson(error), null));
        }

        log.info("📄 Fetching approvals with status: {}", status.name());
        String encryptedResponse = reportApprovalService.getApprovalsByStatus(status, request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }
}
