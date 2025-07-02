package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ReconciliationResultDTO;
import com.chh.trustfort.accounting.dto.TaxDateRangeDTO;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.TaxReconciliationService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(ApiPath.BASE_API)
@Tag(name = "Tax Reconciliation", description = "Handles Tax Posting Reconciliation")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class TaxReconciliationController {

    private final TaxReconciliationService taxReconciliationService;
    private final RequestManager requestManager;
    private final AesService aesService;


    @PostMapping(value = ApiPath.RECONCILE_TAX, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Reconcile Tax Postings for a Date Range")
    public ResponseEntity<String> reconcileTaxes(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        log.info("🧾 Received request to reconcile tax postings...");

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.RECONCILE_TAX.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            return ResponseEntity.badRequest().body(aesService.encrypt(request.payload, request.appUser));
        }

        TaxDateRangeDTO filter = new Gson().fromJson(request.payload, TaxDateRangeDTO.class);
        String encryptedResponse = taxReconciliationService.reconcileTaxPostingsEncrypted(
                filter.getStartDate(), filter.getEndDate(), request.appUser
        );

        return ResponseEntity.ok(encryptedResponse);
    }
}

