package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.CreateDisputeRequest;
import com.chh.trustfort.accounting.dto.DisputeResolutionRequest;
import com.chh.trustfort.accounting.dto.DisputeResponse;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.DisputeService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DisputeController {

    private final DisputeService disputeService;
    private final RequestManager requestManager;
    private final Gson gson;
    private final AesService aesService;

    @PostMapping(value = ApiPath.RAISE_DISPUTE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> raiseDispute(@RequestParam String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.RAISE_DISPUTE.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(
                    aesService.encrypt(SecureResponseUtil.error(response.getResponseMessage(), response.getResponseCode(), "fail"), request.appUser)
            );
        }

        CreateDisputeRequest dto = gson.fromJson(request.payload, CreateDisputeRequest.class);
        DisputeResponse result = disputeService.raiseDispute(dto);

        return ResponseEntity.ok().body(
                aesService.encrypt(SecureResponseUtil.success("Dispute raised successfully", result), request.appUser)
        );
    }

    @PostMapping(value = ApiPath.RESOLVE_DISPUTE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resolveDispute(@RequestParam String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.RESOLVE_DISPUTE.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(
                    aesService.encrypt(SecureResponseUtil.error(response.getResponseMessage(), response.getResponseCode(), "fail"), request.appUser)
            );
        }

        DisputeResolutionRequest dto = gson.fromJson(request.payload, DisputeResolutionRequest.class);
        DisputeResponse result = disputeService.resolveDispute(dto.getReference(), dto.getResolution(), dto.getResolvedBy());

        return ResponseEntity.ok().body(
                aesService.encrypt(SecureResponseUtil.success("Dispute resolved successfully", result), request.appUser)
        );
    }

    @GetMapping(value = ApiPath.GET_DISPUTES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllDisputes(@RequestParam String idToken, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.GET_DISPUTES.getValue(), "", httpRequest, idToken
        );

        if (request.isError) {
            return ResponseEntity.badRequest().body(
                    aesService.encrypt(SecureResponseUtil.error("Unauthorized access", "01", "fail"), request.appUser)
            );
        }

        List<DisputeResponse> result = disputeService.getAllDisputes();
        return ResponseEntity.ok().body(
                aesService.encrypt(SecureResponseUtil.success("Disputes fetched", result), request.appUser)
        );
    }
}
