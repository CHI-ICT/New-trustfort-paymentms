package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.BalanceSheetFilterRequest;
import com.chh.trustfort.accounting.dto.IntegrityCheckResult;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.StatementIntegrityService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Financial Reports", description = "Handles Statement integrity validation")
public class StatementIntegrityController {

    private final StatementIntegrityService integrityService;
    private final AesService aesService;
    private final Gson gson;
    private final RequestManager requestManager;

    @PostMapping(value = ApiPath.VALIDATE_STATEMENT_INTEGRITY,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> validateStatementIntegrity(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        log.info("🔐 Incoming encrypted request to validate statement integrity");

        // ✅ Decrypt and authorize request
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.VALIDATE_STATEMENT_INTEGRITY.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            log.warn("❌ Decryption failed or unauthorized access");
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(aesService.encrypt(gson.toJson(error), null));
        }

        // ✅ Parse decrypted payload
        BalanceSheetFilterRequest filter = gson.fromJson(request.payload, BalanceSheetFilterRequest.class);
        log.info("📄 Validating statement integrity for period: {} to {}", filter.getStartDate(), filter.getEndDate());

        // ✅ Run service logic
        List<IntegrityCheckResult> resultList = integrityService.validateAllStatements(filter);

        // ✅ Encrypt and return response
        return ResponseEntity.ok(aesService.encrypt(gson.toJson(resultList), request.appUser));
    }
}


