package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.PayableInvoiceRequestDTO;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.service.PayableInvoiceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@EncryptResponse
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.BASE_API)
@Tag(name = "Payable Invoice Submission", description = "Submit vendor or admin payable invoices")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class InvoiceSubmissionController {

    private final PayableInvoiceService invoiceService;

    @PostMapping(value = ApiPath.SUBMIT_INVOICE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> submitInvoice(@Valid @RequestBody PayableInvoiceRequestDTO request) {
        try {
            PayableInvoice invoice = invoiceService.submitInvoice(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
        } catch (Exception e) {
            log.error("Failed to submit invoice: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

}
