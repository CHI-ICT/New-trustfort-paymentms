package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.MatchedPairDTO;
import com.chh.trustfort.accounting.dto.MatchingResponseDTO;
import com.chh.trustfort.accounting.enums.MatchingStatus;
import com.chh.trustfort.accounting.enums.ReceiptStatus;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.model.Receivable;
import com.chh.trustfort.accounting.payload.AutoMatchRequestPayload;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.repository.ReceiptRepository;
import com.chh.trustfort.accounting.repository.ReceivableRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.AutoMatcherService;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auto-Matching", description = "Match receipts to receivables")
@Slf4j
public class AutoMatcherController {

    private final AutoMatcherService autoMatcherService;
    private final AesService aesService;
    private final Gson gson;
    private final RequestManager requestManager;

    @PostMapping(value = ApiPath.MATCHER, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> matchReceivables(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        String idToken = authorizationHeader.replace("Bearer ", "").trim();
        log.info("🔐 ID TOKEN: {}", idToken);
        log.info("📥 RAW PAYLOAD (Base64): {}", requestPayload);

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.AUTO_MATCH.getValue(), requestPayload, httpRequest, idToken
        );
        request.appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            String decryptedError = aesService.decrypt(request.payload, request.appUser);
            OmniResponsePayload response = gson.fromJson(decryptedError, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        log.info("📥 Decrypted Payload: {}", request.payload);
        AutoMatchRequestPayload decrypted = gson.fromJson(request.payload, AutoMatchRequestPayload.class);
        String result = autoMatcherService.matchOpenReceivables(decrypted, request.appUser);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
