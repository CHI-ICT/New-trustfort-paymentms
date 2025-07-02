package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.InstallmentRequestDto;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.InstallmentService;
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
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Installment Management", description = "Generate and manage invoice payment installments")
public class InstallmentController {

    private final InstallmentService installmentService;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;

    @PostMapping(value = ApiPath.CREATE_INSTALLMENTS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> generateInstallments(@RequestParam String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.CREATE_INSTALLMENTS.getValue(), requestPayload, httpRequest, idToken);

        if (request.isError) {
            log.warn("❌ Unauthorized attempt to generate installments: {}", request.payload);
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(
                    aesService.encrypt(gson.toJson(error), null));
        }

        InstallmentRequestDto dto = gson.fromJson(request.payload, InstallmentRequestDto.class);
        log.info("📌 Generating {} installments for invoice ID {} by user [{}]",
                dto.getNumberOfInstallments(), dto.getInvoiceId(), request.appUser.getEmail());

        String encryptedResponse = installmentService.generateInstallments(dto, request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }
}