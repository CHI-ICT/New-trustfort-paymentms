package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.CreateReceivableRequest;
import com.chh.trustfort.accounting.dto.ReceivableRequest;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.Receivable;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.ReceivableService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Receivables", description = "Manage receivables and related debit notes")
public class ReceivableController {

    private final ReceivableService receivableService;
    private final AesService aesService;
    private final RequestManager requestManager;
    private final Gson gson;

    @PostMapping(value = ApiPath.CREATE_RECEIVABLE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createReceivable(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.CREATE_RECEIVABLE.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            log.warn("❌ Decryption failed or unauthorized access");
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(aesService.encrypt(gson.toJson(error), null));
        }

        CreateReceivableRequest dto = gson.fromJson(request.payload, CreateReceivableRequest.class);
        String encryptedResponse = receivableService.createReceivable(dto, request.appUser);

        return ResponseEntity.ok(encryptedResponse);
    }

    @GetMapping(value = ApiPath.GET_RECEIVABLE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllReceivables(
            @RequestParam String idToken,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.GET_RECEIVABLE.getValue(), null, httpRequest, idToken
        );

        if (request.isError) {
            log.warn("❌ Decryption failed or unauthorized access");
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(aesService.encrypt(gson.toJson(error), null));
        }

        String encryptedResponse = receivableService.getAllReceivables(request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }
}

