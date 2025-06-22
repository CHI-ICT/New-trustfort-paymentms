package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Util.SecureResponseUtil;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.component.Role;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.payload.OmniResponsePayload;
import com.chh.trustfort.payment.security.AesService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(ApiPath.BASE_API + ApiPath.MOCK_FCMB_BASE)
public class MockFCMBController {

    private final RequestManager requestManager;
    private final Gson gson;
    private final AesService aesService;

    @PostMapping(value = ApiPath.SIMULATE_TRANSFER_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> simulateTransferStatus(
            @RequestParam String idToken,
            @RequestParam String referenceId,
            @RequestParam boolean success,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.MOCK_TRANSFER_STATUS.getValue(), "", httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        log.info("📡 Simulated callback received from FCMB: reference={}, success={}", referenceId, success);
        String message = "Webhook received and simulation logged.";
        return new ResponseEntity<>(aesService.encrypt(message, request.appUser), HttpStatus.OK);
    }
}
