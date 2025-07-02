package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.BankInflowPayload;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.BankInflowSyncService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.BASE_API)
@Tag(name = "Bank Inflow", description = "Simulate and Sync Bank Inflows")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class BankInflowController {

    private final BankInflowSyncService bankInflowSyncService;
    private final RequestManager requestManager;
    private final Gson gson;
    private final AesService aesService;

    @PostMapping(value = ApiPath.SYNC_BANK_INFLOW,
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> syncBankInflow(@RequestParam String idToken,
                                            @RequestBody String requestPayload,
                                            HttpServletRequest httpRequest) {

        log.info("🔐 ID TOKEN: {}", idToken);
        log.info("📥 RAW Payload (Base64): {}", requestPayload);

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.SYNC_BANK_INFLOW.getValue(), requestPayload, httpRequest, idToken
        );

        request.appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            String decryptedError = aesService.decrypt(request.payload, request.appUser);
            OmniResponsePayload errorResponse = gson.fromJson(decryptedError, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(errorResponse.getResponseCode(), errorResponse.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        log.info("📥 Decrypted Payload: {}", request.payload);

        BankInflowPayload decryptedRequest = gson.fromJson(request.payload, BankInflowPayload.class);
        String result = bankInflowSyncService.syncInflow(decryptedRequest, request.appUser);

        return ResponseEntity.ok(result);
    }
}

