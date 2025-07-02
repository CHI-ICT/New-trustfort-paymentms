package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.PayableInvoiceRequestDTO;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.PayableInvoiceService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Tag(name = "Payable Invoice Submission", description = "Submit vendor or admin payable invoices")
@Slf4j
public class InvoiceSubmissionController {

    private final PayableInvoiceService invoiceService;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;

    @PostMapping(value = ApiPath.SUBMIT_INVOICE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> submitInvoice(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.SUBMIT_INVOICE.getValue(), requestPayload, httpRequest, idToken);

        if (request.isError) {
            log.warn("❌ Unauthorized invoice submission: {}", request.payload);
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(
                    aesService.encrypt(gson.toJson(response), null)
            );
        }

        PayableInvoiceRequestDTO dto = gson.fromJson(request.payload, PayableInvoiceRequestDTO.class);
        log.info("📥 Processing invoice submission for vendor: {}", dto.getVendorName());

        String encryptedResponse = invoiceService.submitInvoice(dto, request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }
}