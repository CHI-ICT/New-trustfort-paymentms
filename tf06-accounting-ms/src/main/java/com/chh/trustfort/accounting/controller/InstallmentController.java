package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.InstallmentRequestDto;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import com.chh.trustfort.accounting.service.InstallmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
@Slf4j
@EncryptResponse
@Tag(name = "Installment Management", description = "Generate and manage invoice payment installments")
@SecurityRequirement(name = "bearerAuth")
public class InstallmentController {

    private final InstallmentService installmentService;

    private final PayableInvoiceRepository payableInvoiceRepository;


    @PostMapping(value = ApiPath.CREATE_INSTALLMENTS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> generateInstallments(@RequestBody InstallmentRequestDto request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Fetch the invoice
            PayableInvoice invoice = payableInvoiceRepository.findById(request.getInvoiceId())
                    .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + request.getInvoiceId()));

            // Generate installments
            installmentService.generateInstallments(request.getInvoiceId(), request.getNumberOfInstallments());

            // Prepare success response
            response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Payment installments generated successfully.");
            response.put("invoiceId", invoice.getId());
            response.put("installments", request.getNumberOfInstallments());
            response.put("invoiceDetails", Map.of(
                    "invoiceNumber", invoice.getInvoiceNumber(),
                    "vendorName", invoice.getVendorName(),
                    "amount", invoice.getAmount(),
                    "dueDate", invoice.getDueDate(),
                    "status", invoice.getStatus()
            ));

            return ResponseEntity.ok(response);
        } catch (IllegalStateException ex) {
            response.put("status", "error");
            response.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Failed to generate installments for invoice ID {}: {}", request.getInvoiceId(), e.getMessage());
            response.put("status", "error");
            response.put("message", "Failed to generate payment installments.");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
