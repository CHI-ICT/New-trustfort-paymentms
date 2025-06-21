package com.chh.trustfort.payment.controller.facility;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Util.SecureResponseUtil;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.enums.Role;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.model.facility.ApprovalRule;
import com.chh.trustfort.payment.payload.DeleteApprovalRulePayload;
import com.chh.trustfort.payment.payload.OmniResponsePayload;
import com.chh.trustfort.payment.payload.UpdateApproverPayload;
import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.facility.ApprovalRuleAdminService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Approval Rule", description = "Approval Rule REST API")
@RefreshScope
public class ApprovalRuleController {
    @Autowired
    RequestManager requestManager;

    @Autowired
    AesService aesService;

    @Autowired
    Gson gson;

    @Autowired
    private ApprovalRuleAdminService ruleService;

    @GetMapping(value = ApiPath.GET_ALL_APPROVAL_RULES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processGetAllApprovalRules(String idToken, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.VIEW_APPROVAL_RULE.getValue(), null, httpRequest, idToken);
        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);

            return new ResponseEntity<>(aesService.encrypt(SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.UNAUTHORIZED)), request.appUser), HttpStatus.OK);
        }
        return new ResponseEntity<>(aesService.encrypt(gson.toJson(ruleService.getAllRules()), request.appUser), HttpStatus.OK);
    }

    @PostMapping(value = ApiPath.CREATE_APPROVAL_RULE, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.CREATE_APPROVAL_RULE.getValue(), requestPayload, httpRequest, idToken);
        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)), HttpStatus.OK);
        }

        String ruleApprovalResponse = ruleService.createRule(request.payload, idToken, request.appUser);
        return new ResponseEntity<>(aesService.encrypt(ruleApprovalResponse, request.appUser), HttpStatus.OK);
    }

    @PutMapping(value = ApiPath.UPDATE_APPROVER, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateApprover(String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.UPDATE_APPROVER.getValue(), requestPayload, httpRequest, idToken);
        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)), HttpStatus.OK);
        }

        UpdateApproverPayload payload = gson.fromJson(request.payload, UpdateApproverPayload.class);
        ApprovalRule updated = ruleService.updateApprover(payload.getRuleId(), payload.getNewApproverId());
        return new ResponseEntity<>(aesService.encrypt(gson.toJson(updated), request.appUser), HttpStatus.OK);
    }

    @DeleteMapping(value = ApiPath.DELETE_APPROVAL_RULE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> softDelete(@RequestParam("id") Long id, @RequestHeader("Authorization") String idToken, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.DELETE_APPROVAL_RULE.getValue(), null, httpRequest, idToken);
        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)), HttpStatus.OK);
        }

        ruleService.softDeleteRule(id);
        return new ResponseEntity<>(aesService.encrypt("Rule deleted successfully", request.appUser), HttpStatus.OK);
    }
}
