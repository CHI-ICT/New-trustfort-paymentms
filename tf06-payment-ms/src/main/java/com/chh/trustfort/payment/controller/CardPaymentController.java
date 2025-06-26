package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Util.SecureResponseUtil;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.InitiatePaymentRequest;
import com.chh.trustfort.payment.dto.PaystackWebhookPayload;
import com.chh.trustfort.payment.enums.PaymentGateway;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.payload.OmniResponsePayload;
import com.chh.trustfort.payment.security.AesService;
//import com.chh.trustfort.payment.service.FlutterwaveService;
import com.chh.trustfort.payment.service.PaystackService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

// Refactored CardPaymentController.java

@RestController
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class CardPaymentController {

    private final PaystackService paystackService;
//    private final FlutterwaveService flutterwaveService;
    private final RequestManager requestManager;
    private final Gson gson;
    private final AesService aesService;

    @PostMapping(value = ApiPath.INITIATE_CARD_PAYMENT, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> initiateCardPayment(@RequestHeader(name = "Authorization") String idToken,
                                                 @RequestBody String requestPayload,
                                                 HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                "INITIATE_CARD_PAYMENT", requestPayload, httpRequest, idToken);

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(),
                            String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK);
        }

        try {
            InitiatePaymentRequest paymentRequest = gson.fromJson(request.payload, InitiatePaymentRequest.class);

            String redirectUrl;
            if (paymentRequest.getGateway() == PaymentGateway.PAYSTACK) {
                redirectUrl = paystackService.initiatePayment(paymentRequest);
//            } else if (paymentRequest.getGateway() == PaymentGateway.FLUTTERWAVE) {
//                redirectUrl = flutterwaveService.initiatePayment(paymentRequest);
            } else {
                return new ResponseEntity<>(SecureResponseUtil.error("400", "Unsupported gateway", "400"), HttpStatus.OK);
            }

            Map<String, String> result = Map.of("authorizationUrl", redirectUrl);
            return new ResponseEntity<>(aesService.encrypt(result.toString(), request.appUser), HttpStatus.OK);

        } catch (Exception e) {
            log.error("Failed to initiate card payment", e);
            return new ResponseEntity<>(SecureResponseUtil.error("500", "Error initiating payment", "500"), HttpStatus.OK);
        }
    }
}

