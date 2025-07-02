package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ApiResponse;
import com.chh.trustfort.accounting.enums.PayoutCategory;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.PayoutClassifierService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payout Classification", description = "Classify invoice payouts by category")
public class PayoutClassificationController {

    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;
    private final PayableInvoiceRepository invoiceRepo;
    private final PayoutClassifierService classifierService;

    @PostMapping(value = ApiPath.CLASSIFY_INVOICE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> classifyInvoice(
            @RequestParam String idToken,
            @RequestParam String invoiceNumber,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.PAYOUT_CLASSIFIER.getValue(), "", httpRequest, idToken
        );

        if (request.isError) {
            log.warn("❌ Authorization failed: {}", request.payload);
            return ResponseEntity.status(401).body(
                    aesService.encrypt(request.payload, request.appUser)
            );
        }

        PayableInvoice invoice = invoiceRepo.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        String encryptedResponse = classifierService.classifyInvoice(invoice, request.appUser);
        invoiceRepo.save(invoice); // Save updated classification
        return ResponseEntity.ok(encryptedResponse);
    }
}