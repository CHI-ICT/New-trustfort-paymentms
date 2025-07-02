// PaymentMovementController.java
package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.PaymentMovementRequest;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.PaymentMovementService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Tag(name = "Payment Movement", description = "Move payment balance between receivables")
@Slf4j
public class PaymentMovementController {

    private final PaymentMovementService paymentMovementService;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;

    @PostMapping(value = ApiPath.MOVE_PAYMENT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> movePayment(@RequestParam String idToken,
                                              @RequestBody String requestPayload,
                                              HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.MOVE_PAYMENT.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            log.warn("❌ Decryption failed or unauthorized access");
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(aesService.encrypt(gson.toJson(error), null));
        }

        PaymentMovementRequest movementRequest = gson.fromJson(request.payload, PaymentMovementRequest.class);
        String encryptedResponse = paymentMovementService.movePayment(movementRequest, request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }
}