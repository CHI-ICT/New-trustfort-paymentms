package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Util.SecureResponseUtil;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.enums.Role;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.payload.FundWalletRequestPayload;
import com.chh.trustfort.payment.payload.OmniResponsePayload;
import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.ServiceImpl.FlutterwavePaymentServiceImpl;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Flutterwave Payment", description = "Endpoints for initiating Flutterwave wallet funding")
public class FlutterwavePaymentController {

    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;
    private final FlutterwavePaymentServiceImpl flutterwavePaymentService;

    @PostMapping(value = ApiPath.INITIATE_FLW_PAYMENT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> initiateFlutterwavePayment(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestHeader("idToken") String idToken,
            @RequestBody FundWalletRequestPayload requestPayload,
            HttpServletRequest httpRequest) {

        // 🔐 Clean up Authorization Bearer token
        String token = authorizationHeader.replace("Bearer ", "").trim();

        // 🔐 Validate request (no decryption for plain payload)
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.FUND_WALLET.getValue(), null, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.ok(SecureResponseUtil.error(
                    response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value())));
        }

        // 🔄 Proceed with Flutterwave Payment
        String encryptedResponse = flutterwavePaymentService.initiateFlutterwavePayment(requestPayload, request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }
}

