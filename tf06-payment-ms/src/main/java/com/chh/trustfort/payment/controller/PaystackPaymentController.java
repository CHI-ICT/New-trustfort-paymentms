package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.enums.Role;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.PaymentReference;
import com.chh.trustfort.payment.payload.FundWalletRequestPayload;
import com.chh.trustfort.payment.payload.OmniResponsePayload;
import com.chh.trustfort.payment.repository.PaymentReferenceRepository;
import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.ServiceImpl.PaystackPaymentService;
import com.chh.trustfort.payment.service.ServiceImpl.PaystackPaymentServiceImpl;
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
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Paystack Payment", description = "Handles Paystack payment initiation and verification")
public class PaystackPaymentController {

    private final PaystackPaymentService paystackPaymentService;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;
    private final PaymentReferenceRepository paymentReferenceRepository;

    @PostMapping(value = ApiPath.INITIATE_PAYSTACK_PAYMENT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> initiatePaystackPayment(
            @RequestParam String idToken,
            @RequestBody String encryptedPayload,
            HttpServletRequest httpRequest
    ) {
        log.info("🚀 Paystack payment initiation called");

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.FUND_WALLET.getValue(), encryptedPayload, httpRequest, idToken);

        if (request.isError || request.appUser == null) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    aesService.encrypt(gson.toJson(response), request.appUser));
        }

        FundWalletRequestPayload payload = gson.fromJson(request.payload, FundWalletRequestPayload.class);
        String encryptedResponse = paystackPaymentService.initiatePaystackPayment(payload, request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }

    @GetMapping(value = ApiPath.VERIFY_PAYSTACK_PAYMENT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifyPaystackPayment(
            @RequestParam String txRef,
            @RequestParam String idToken,
            HttpServletRequest httpRequest
    ) {
        log.info("🔍 Verifying Paystack payment for txRef: {}", txRef);

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.FUND_WALLET.getValue(), null, httpRequest, idToken);

        if (request.isError || request.appUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized or invalid session.");
        }

        boolean verified = paystackPaymentService.verifyPaystackPayment(txRef);
        String result = verified ? "✅ Payment verified and wallet credited" : "❌ Verification failed";

        return ResponseEntity.ok(aesService.encrypt(result, request.appUser));
    }

    @PostMapping(value = ApiPath.REVERIFY_PAYSTACK, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> retryPayment(
            @RequestParam String txRef,
            @RequestParam String idToken,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.FUND_WALLET.getValue(), null, httpRequest, idToken);

        if (request.isError || request.appUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        Optional<PaymentReference> refOpt = paymentReferenceRepository.findByTxRef(txRef);
        if (refOpt.isEmpty()) return ResponseEntity.badRequest().body("❌ Invalid txRef");

        boolean success = paystackPaymentService.reverifyAndCredit(refOpt.get());
        String msg = success ? "✅ Reverified and credited" : "❌ Retry failed";

        return ResponseEntity.ok(aesService.encrypt(msg, request.appUser));
    }
}
