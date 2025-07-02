package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ApiResponse;
import com.chh.trustfort.accounting.dto.BankPaymentRequestDTO;
import com.chh.trustfort.accounting.enums.PaymentStatus;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.model.PaymentLog;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.repository.PayableInvoiceRepository;
import com.chh.trustfort.accounting.repository.PaymentLogRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.BankPaymentService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Bank Payment Integration", description = "Secure mock transfer for payable invoices")
@Slf4j
public class BankPaymentController {

    private final PayableInvoiceRepository invoiceRepo;
    private final BankPaymentService bankPaymentService;
    private final PaymentLogRepository paymentLogRepo;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;

    @PostMapping(value = ApiPath.SIMULATE_PAYMENT,
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> simulatePayment(@RequestParam String idToken,
                                             @RequestBody String requestPayload,
                                             HttpServletRequest httpRequest) {

        log.info("🔐 ID TOKEN: {}", idToken);
        log.info("📥 RAW Encrypted Payload: {}", requestPayload);

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.SIMULATE_PAYMENT.getValue(), requestPayload, httpRequest, idToken
        );

        AppUser appUser = request.appUser;
        appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            OmniResponsePayload errorResponse = gson.fromJson(
                    aesService.decrypt(request.payload, appUser),
                    OmniResponsePayload.class
            );
            return new ResponseEntity<>(
                    SecureResponseUtil.error(errorResponse.getResponseCode(), errorResponse.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        log.info("📥 Decrypted Payload: {}", request.payload);
        JsonObject json = gson.fromJson(request.payload, JsonObject.class);
        String invoiceNumber = json.get("invoiceNumber").getAsString();

        PayableInvoice invoice = invoiceRepo.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found for number: " + invoiceNumber));

        BankPaymentRequestDTO paymentRequest = BankPaymentRequestDTO.builder()
                .beneficiaryName(invoice.getVendorName())
                .beneficiaryAccountNumber("1234567890")
                .beneficiaryBankCode("999")
                .amount(invoice.getAmount())
                .narration("Payment for invoice " + invoice.getInvoiceNumber())
                .reference("PAY-" + UUID.randomUUID())
                .build();

        boolean result = bankPaymentService.initiateTransfer(paymentRequest);

        paymentLogRepo.save(PaymentLog.builder()
                .invoiceNumber(invoice.getInvoiceNumber())
                .scheduleId(null) // update if available
                .amount(invoice.getAmount())
                .reference(paymentRequest.getReference())
                .status(result ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .attemptedAt(LocalDateTime.now())
                .build());

        OmniResponsePayload response = new OmniResponsePayload();
        response.setResponseCode("00");
        response.setResponseMessage("Mock payment " + (result ? "succeeded" : "failed"));
//        response.setData(Map.of(
//                "reference", paymentRequest.getReference(),
//                "status", result ? "SUCCESS" : "FAILED"
//        ));

        return ResponseEntity.ok(aesService.encrypt(gson.toJson(response), appUser));
    }
}

