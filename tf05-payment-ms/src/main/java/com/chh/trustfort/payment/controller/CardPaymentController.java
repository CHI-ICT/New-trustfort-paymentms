package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.InitiatePaymentRequest;
import com.chh.trustfort.payment.dto.PaystackWebhookPayload;
import com.chh.trustfort.payment.enums.PaymentGateway;
import com.chh.trustfort.payment.service.FlutterwaveService;
import com.chh.trustfort.payment.service.PaystackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(ApiPath.BASE_API)
public class CardPaymentController {

    @Autowired
    private PaystackService paystackService;

    @Autowired
    private FlutterwaveService flutterwaveService;

    @PostMapping(value = ApiPath.INITIATE_CARD_PAYMENT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> initiateCardPayment(@RequestBody InitiatePaymentRequest request) {
        if (request.getGateway() == PaymentGateway.PAYSTACK) {
            String redirectUrl = paystackService.initiatePayment(request);
            return ResponseEntity.ok(Map.of("authorizationUrl", redirectUrl));
        }

        if (request.getGateway() == PaymentGateway.FLUTTERWAVE) {
            String redirectUrl = flutterwaveService.initiatePayment(request);
            return ResponseEntity.ok(Map.of("authorizationUrl", redirectUrl));
        }

        return ResponseEntity.badRequest().body("Unsupported payment gateway");
    }
}
