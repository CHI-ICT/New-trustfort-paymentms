package com.chh.trustfort.payment.controller.facility;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Util.SecureResponseUtil;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.ApprovalActionRequest;
import com.chh.trustfort.payment.enums.Role;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.payload.OmniResponsePayload;
import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.facility.ICreditApprovalService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CreditApprovalController {

    @Autowired
    private ICreditApprovalService approvalService;

    @Autowired
    private AesService aesService;

    @Autowired
    private Gson gson;

    @Autowired
    private com.chh.trustfort.payment.component.RequestManager requestManager;

    @PostMapping(value = ApiPath.ACT_ON_APPROVAL, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> actOnApproval(String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.APPROVE_CREDIT_LINE.getValue(), requestPayload, httpRequest, idToken);
        if(request.isError){
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)) , HttpStatus.OK);
        }

        ApprovalActionRequest approvalRequest = gson.fromJson(request.payload, ApprovalActionRequest.class);
        approvalService.actOnApproval(approvalRequest);

        return new ResponseEntity<>(aesService.encrypt("Approval action processed", request.appUser), HttpStatus.OK);
    }

    @GetMapping(value = ApiPath.GET_PENDING_APPROVALS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPending(String idToken, HttpServletRequest httpRequest, @PathVariable Long approverId) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.VIEW_CREDIT_LINE.getValue(), null, httpRequest, idToken);
        if(request.isError){
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);

            return new ResponseEntity<>(aesService.encrypt(SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.UNAUTHORIZED)),request.appUser) , HttpStatus.OK);
        }
        return new ResponseEntity<>(aesService.encrypt(gson.toJson(approvalService.getPendingApprovals(approverId)), request.appUser), HttpStatus.OK);
    }
}
