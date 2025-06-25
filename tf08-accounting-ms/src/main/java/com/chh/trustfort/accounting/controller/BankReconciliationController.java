package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;

import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.BankReconciliationFilterDTO;
import com.chh.trustfort.accounting.dto.ReconciliationResultDTO;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.BankReconciliationService;
//import com.chh.trustfort.payment.Util.SecureResponseUtil;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Tag(name = "Bank Reconciliation", description = "Sync and match ledger with bank inflows")
@RefreshScope
@RequiredArgsConstructor
@RequestMapping(ApiPath.BASE_API)
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class BankReconciliationController {

    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;
    private final BankReconciliationService reconciliationService;

    @PostMapping(value = ApiPath.RECONCILE_BANK, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> reconcileBank(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.RECONCILE_BANK.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    aesService.encrypt(SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.UNAUTHORIZED)), request.appUser),
                    HttpStatus.OK
            );
        }

        BankReconciliationFilterDTO filter = gson.fromJson(request.payload, BankReconciliationFilterDTO.class);
        String encryptedResponse = reconciliationService.reconcileBankWithLedger(filter.getStartDate(), filter.getEndDate(), request.appUser).toString();
        return new ResponseEntity<>(encryptedResponse, HttpStatus.OK);
    }
}
