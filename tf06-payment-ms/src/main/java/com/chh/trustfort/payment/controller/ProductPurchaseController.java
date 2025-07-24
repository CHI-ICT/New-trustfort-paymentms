package com.chh.trustfort.payment.controller;


import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Responses.ErrorResponse;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.constant.ApiPath;

import com.chh.trustfort.payment.dto.ProductPurchaseDTO;
import com.chh.trustfort.payment.dto.PurchaseIntentDTO;
import com.chh.trustfort.payment.dto.UnifiedPurchaseRequestPayload;
import com.chh.trustfort.payment.enums.PaymentMethod;
import com.chh.trustfort.payment.enums.Role;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.payload.OmniResponsePayload;
import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.PurchaseIntentService;
import com.chh.trustfort.payment.service.ServiceImpl.FlutterwavePaymentService;
import com.chh.trustfort.payment.service.ServiceImpl.PaystackPaymentServiceImpl;
import com.chh.trustfort.payment.service.WalletService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ProductPurchaseController {

    private final AesService aesService;
    private final Gson gson;
    private final RequestManager requestManager;
    private final WalletService walletService;
    private final PaystackPaymentServiceImpl paystackPaymentService;
    private final FlutterwavePaymentService flutterwavePaymentService;
    private final PurchaseIntentService purchaseIntentService;

    @PostMapping(value = ApiPath.INITIATE_PRODUCT_PURCHASE, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> initiatePurchaseViaPaystack(
            @RequestHeader String idToken,
            @RequestBody String encryptedPayload,
            HttpServletRequest httpRequest
    ) {
        log.info("🚀 Product purchase via Paystack initiated");

        // ✅ Validate request using standard format
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.FUND_WALLET.getValue(), encryptedPayload, httpRequest, idToken
        );

        if (request.isError || request.appUser == null) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(aesService.encrypt(gson.toJson(response), request.appUser));
        }

        // ✅ Decrypted payload is already inside `request.payload`
        PurchaseIntentDTO dto = gson.fromJson(request.payload, PurchaseIntentDTO.class);

        // 🔁 Generate transaction reference
        String txRef = "PROD-" + System.currentTimeMillis();

        // 💾 Save intent before payment
        purchaseIntentService.savePurchaseIntent(dto, txRef);

        // 🔗 Initiate Paystack payment
        String encryptedResponse = paystackPaymentService.initiatePaystackPaymentForProduct(dto, txRef, request.appUser);

        return ResponseEntity.ok(encryptedResponse);
    }

    @GetMapping(value = ApiPath.VERIFY_PRODUCT_PAYMENT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifyProductPayment(
            @RequestParam String txRef,
            @RequestHeader String idToken,
            HttpServletRequest httpRequest
    ) {
        log.info("🔍 Verifying Paystack product payment for txRef: {}", txRef);

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.FUND_WALLET.getValue(), null, httpRequest, idToken);

        if (request.isError || request.appUser == null) {
            OmniResponsePayload errorResponse = new OmniResponsePayload();
            errorResponse.setResponseCode("06");
            errorResponse.setResponseMessage("Unauthorized or invalid session.");
            return ResponseEntity.ok(aesService.encrypt(gson.toJson(errorResponse), null));
        }

        boolean verified = paystackPaymentService.verifyPaystackProductPayment(txRef);

        OmniResponsePayload response = new OmniResponsePayload();
        if (verified) {
            response.setResponseCode("00");
            response.setResponseMessage("✅ Payment verified and product marked as paid.");
        } else {
            response.setResponseCode("06");
            response.setResponseMessage("❌ Verification failed for product payment.");
        }

        return ResponseEntity.ok(aesService.encrypt(gson.toJson(response), request.appUser));
    }

    @PostMapping(value = ApiPath.UNIFIED_PURCHASE, consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> purchaseProductUnified(
            @RequestHeader String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        log.info("🛒 Unified purchase endpoint called");

        // 🔐 Validate and decrypt
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.FUND_WALLET.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError || request.appUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(aesService.encrypt(gson.toJson(new ErrorResponse("Unauthorized", "06")), null));
        }

        AppUser appUser = request.appUser;
        UnifiedPurchaseRequestPayload payload = gson.fromJson(request.payload, UnifiedPurchaseRequestPayload.class);

        switch (payload.getPaymentMethod()) {
            case WALLET:
                // 🔁 Route to wallet deduction
                ProductPurchaseDTO walletDto = new ProductPurchaseDTO();
                walletDto.setUserId(payload.getUserId());
                walletDto.setAmount(payload.getAmount());
                walletDto.setProductName(payload.getProductName());
                walletDto.setNarration(payload.getNarration());
                walletDto.setStringifiedData(payload.getStringifiedData());
                return ResponseEntity.ok(walletService.deductWalletForProductPurchase(walletDto, appUser, appUser));

            case PAYSTACK:
            case FLUTTERWAVE:
            case OPEN_BANKING:
                // 🔁 Route to gateway payment
                PurchaseIntentDTO dto = new PurchaseIntentDTO();
                dto.setUserId(payload.getUserId());
                dto.setAmount(payload.getAmount());
                dto.setStringifiedData(payload.getStringifiedData());

                String txRef = "PROD-" + System.currentTimeMillis();
                purchaseIntentService.savePurchaseIntent(dto, txRef);

                String encryptedResponse;
                if (payload.getPaymentMethod() == PaymentMethod.PAYSTACK) {
                    encryptedResponse = paystackPaymentService.initiatePaystackPaymentForProduct(dto, txRef, appUser);
                } else {
                    encryptedResponse = flutterwavePaymentService.initiateFlutterwavePaymentForProduct(dto, txRef, appUser);
                }

                return ResponseEntity.ok(encryptedResponse);

            default:
                return ResponseEntity.ok(aesService.encrypt(
                        gson.toJson(new ErrorResponse("Invalid payment method", "91")), appUser));
        }
    }


}