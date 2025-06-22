// PaymentMovementController.java
package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.PaymentMovementRequest;
import com.chh.trustfort.accounting.service.PaymentMovementService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(ApiPath.BASE_API)
@Slf4j
public class PaymentMovementController {

    private final PaymentMovementService paymentMovementService;

    @PostMapping(value = ApiPath.MOVE_PAYMENT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> movePayment(@RequestBody PaymentMovementRequest request) {
        paymentMovementService.movePayment(request);
        return ResponseEntity.ok("Payment movement successful");
    }
}