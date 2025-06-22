package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ApiResponse;
import com.chh.trustfort.accounting.enums.PayoutCategory;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import com.chh.trustfort.accounting.service.PayoutClassifierService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@EncryptResponse
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
@Tag(name = "Payout Classification", description = "Classify invoice payouts by category")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class PayoutClassificationController {

    private final PayableInvoiceRepository invoiceRepo;
    private final PayoutClassifierService classifierService;


    @PostMapping(value = ApiPath.CLASSIFY_INVOICE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> classifyInvoice(@PathVariable String invoiceNumber) {
        PayableInvoice invoice = invoiceRepo.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        PayoutCategory category = classifierService.classify(invoice);
        invoice.setPayoutCategory(category);
        invoiceRepo.save(invoice);

        return ResponseEntity.ok(ApiResponse.success("Invoice classified as " + category.name(), invoice));
    }
}
