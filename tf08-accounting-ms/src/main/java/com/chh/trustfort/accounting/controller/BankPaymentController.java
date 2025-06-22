package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ApiResponse;
import com.chh.trustfort.accounting.dto.BankPaymentRequestDTO;
import com.chh.trustfort.accounting.enums.PaymentStatus;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.model.PaymentLog;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import com.chh.trustfort.accounting.repository.PaymentLogRepository;
import com.chh.trustfort.accounting.service.BankPaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@EncryptResponse
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Bank Payment Integration", description = "Secure mock transfer for payable invoices")
@Slf4j
public class BankPaymentController {

    private final PayableInvoiceRepository invoiceRepo;
    private final BankPaymentService bankPaymentService;
    private final PaymentLogRepository paymentLogRepo;


    @PostMapping(value = ApiPath.SIMULATE_PAYMENT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> simulatePayment(@PathVariable String invoiceNumber) {

        PayableInvoice invoice = invoiceRepo.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        BankPaymentRequestDTO request = BankPaymentRequestDTO.builder()
                .beneficiaryName(invoice.getVendorName())
                .beneficiaryAccountNumber("1234567890")
                .beneficiaryBankCode("999")
                .amount(invoice.getAmount())
                .narration("Payment for invoice " + invoice.getInvoiceNumber())
                .reference("PAY-" + UUID.randomUUID())
                .build();

        boolean result = bankPaymentService.initiateTransfer(request);

        paymentLogRepo.save(PaymentLog.builder()
                .invoiceNumber(invoice.getInvoiceNumber())
                .scheduleId(null) // Replace with actual scheduleId if available
                .amount(invoice.getAmount())
                .reference(request.getReference())
                .status(result ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .attemptedAt(LocalDateTime.now())
                .build());

        return ResponseEntity.ok(ApiResponse.success("Mock payment " + (result ? "succeeded" : "failed"), null));
    }
}
