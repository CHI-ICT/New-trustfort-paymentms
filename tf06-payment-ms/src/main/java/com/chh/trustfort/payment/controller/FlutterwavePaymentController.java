package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Util.SecureResponseUtil;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.VerifyFlutterwaveRequest;
import com.chh.trustfort.payment.enums.Role;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.PaymentReference;
import com.chh.trustfort.payment.payload.FundWalletRequestPayload;
import com.chh.trustfort.payment.payload.OmniResponsePayload;
import com.chh.trustfort.payment.payload.TokenData;
import com.chh.trustfort.payment.repository.AppUserRepository;
import com.chh.trustfort.payment.repository.PaymentReferenceRepository;
import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.ServiceImpl.FlutterwavePaymentServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Flutterwave Payment", description = "Endpoints for initiating Flutterwave wallet funding")
public class FlutterwavePaymentController {

    private final RequestManager requestManager;
    private final AppUserRepository appUserRepository;
    private final AesService aesService;
    private final PaymentReferenceRepository paymentReferenceRepository;
    private final Gson gson;
    private final FlutterwavePaymentServiceImpl flutterwavePaymentService;
    private final FlutterwavePaymentServiceImpl flutterwavePaymentServiceImpl;


    @PostMapping(value = ApiPath.ENCRYPT_PAYLOAD, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> encryptTestPayload(
            @RequestBody FundWalletRequestPayload requestPayload,
            @RequestHeader("idToken") String idToken,
            HttpServletRequest httpRequest) {

        // Use validateRequest to extract AppUser
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.FUND_WALLET.getValue(), null, httpRequest, idToken
        );

        if (request.isError || request.appUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authorization.");
        }

        AppUser appUser = request.appUser;

        // Encrypt the raw payload (no decryption step needed)
        String encrypted = aesService.encrypt(gson.toJson(requestPayload), appUser);

        log.info("🔐 Encrypted payload: {}", encrypted);
        return ResponseEntity.ok(Map.of("encryptedPayload", encrypted));
    }

    @GetMapping(ApiPath.FLUTTERWAVE_REDIRECT)
    public void handleFlutterwaveRedirect(
            @RequestParam("status") String status,
            @RequestParam("tx_ref") String txRef,
            @RequestParam("transaction_id") String transactionId,
            HttpServletResponse response) throws IOException {

        log.info("🔁 Flutterwave redirect received: status={}, tx_ref={}, transaction_id={}", status, txRef, transactionId);

        boolean verified = flutterwavePaymentServiceImpl.verifyFlutterwavePayment(txRef, transactionId);

        String webhookTestUrl = "https://webhook.site/9c0dd086-b3a5-46b6-9df7-b43da5d8415b"; // replace with your actual generated URL

        if (verified) {
            response.sendRedirect(webhookTestUrl + "?ref=" + txRef + "&status=success");
        } else {
            response.sendRedirect(webhookTestUrl + "?ref=" + txRef + "&status=failure");
        }
        }


    @PostMapping(value = ApiPath.INITIATE_FLW_PAYMENT, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> initiateFlutterwavePayment(
                @RequestHeader("Authorization") String authorizationHeader,
                @RequestHeader("idToken") String idToken,
                @RequestBody String encryptedPayload,
                HttpServletRequest httpRequest) {

        log.info("🚀 Flutterwave Payment Endpoint hit with encrypted payload");

            // 🔐 Clean Authorization Header
            String token = authorizationHeader.replace("Bearer ", "").trim();

        // 🔐 Validate request first
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.FUND_WALLET.getValue(), encryptedPayload, httpRequest, idToken
        );

        if (request.isError || request.appUser == null) {
            log.warn("⛔ Authorization failed.");
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.ok(SecureResponseUtil.error(
                    response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.UNAUTHORIZED.value()))
            );
        }
// ✅ Decrypt and parse the actual payload
        FundWalletRequestPayload decryptedRequest = gson.fromJson(request.payload, FundWalletRequestPayload.class);


        // 🚀 Proceed with payment
        String encryptedResponse = flutterwavePaymentServiceImpl.initiateFlutterwavePayment(decryptedRequest, request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }


    @GetMapping(value = ApiPath.VERIFY_FLW_PAYMENT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifyFlutterPayment(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestHeader("idToken") String idToken,
            @RequestParam("tx_ref") String txRef,
            @RequestParam("transaction_id") String transactionId,
            HttpServletRequest httpRequest) {

        // Step 1: Validate request
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.FUND_WALLET.getValue(), null, httpRequest, idToken
        );

        if (request.isError || request.appUser == null) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.ok(SecureResponseUtil.error(
                    response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.UNAUTHORIZED.value()))
            );
        }

        // Step 2: Call verification service
        boolean verified = flutterwavePaymentService.verifyFlutterwavePayment(txRef, transactionId);

        // Step 3: Return encrypted result
        String result = verified ? "Payment verified and wallet credited" : "Verification failed or already verified";
        return ResponseEntity.ok(aesService.encrypt(result, request.appUser));
    }


    @PostMapping(value = ApiPath.REVERIFY_FLUTTER, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> testReverify(@RequestParam String txRef) {
        try {
            log.info("🔁 Initiating reverification for txRef: {}", txRef);

            Optional<PaymentReference> refOpt = paymentReferenceRepository.findByTxRef(txRef);
            if (refOpt.isEmpty()) {
                log.warn("❌ PaymentReference not found for txRef: {}", txRef);
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "fail",
                        "message", "Reference not found for txRef: " + txRef
                ));
            }

            PaymentReference reference = refOpt.get();
            boolean result = flutterwavePaymentService.reverifyAndCredit(reference);

            if (result) {
                log.info("✅ Reverification succeeded for txRef: {}", txRef);
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Reverified and wallet credited successfully"
                ));
            } else {
                log.warn("⚠️ Reverification failed for txRef: {}", txRef);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                        "status", "fail",
                        "message", "Reverification failed or mismatch in details"
                ));
            }

        } catch (Exception ex) {
            log.error("❌ Exception during reverification for txRef {}: {}", txRef, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Unexpected error occurred during reverification"
            ));
        }
    }


    @GetMapping("/admin/reverify-payment")
    public ResponseEntity<?> reverifyFailedTx(@RequestParam String txRef) {
        Optional<PaymentReference> refOpt = paymentReferenceRepository.findByTxRef(txRef);
        if (refOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Payment reference not found");
        }

        PaymentReference ref = refOpt.get();
        boolean success = flutterwavePaymentService.reverifyAndCredit(ref);

        if (success) {
            return ResponseEntity.ok("Payment reverified and credited.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Verification failed again.");
        }
    }

    @PostMapping(value = ApiPath.RETRY_FLW_PAYMENT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> retryPaymentViaFrontend(
            @RequestParam String idToken,
            @RequestBody String encryptedPayload,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.FUND_WALLET.getValue(), encryptedPayload, httpRequest, idToken
        );

        if (request.isError) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid request.");
        }

        String txRef = gson.fromJson(request.payload, JsonObject.class).get("txRef").getAsString();
        Optional<PaymentReference> refOpt = paymentReferenceRepository.findByTxRef(txRef);

        if (refOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Reference not found");
        }

        boolean result = flutterwavePaymentService.reverifyAndCredit(refOpt.get());
        String responseMsg = result ? "✅ Payment verified & credited" : "❌ Verification failed again";

        return ResponseEntity.ok(aesService.encrypt(responseMsg, request.appUser));
    }


}

