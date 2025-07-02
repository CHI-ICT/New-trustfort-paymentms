package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.PaymentSchedule;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.PaymentSchedulerService;
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
import java.util.List;

@RestController
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payment Scheduling", description = "Handles creation of payment schedules")
public class PaymentScheduleController {

    private final PaymentSchedulerService schedulerService;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;

    @PostMapping(value = ApiPath.SCHEDULE_PAYMENTS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> schedulePayments(
            @RequestParam String idToken,
            @RequestParam Long invoiceId,
            @RequestParam(defaultValue = "2") int installments,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.SCHEDULE_PAYMENTS.getValue(), "", httpRequest, idToken
        );

        if (request.isError) {
            log.warn("🔒 Unauthorized schedule attempt. Payload: {}", request.payload);
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    aesService.encrypt(gson.toJson(error), request.appUser)
            );
        }

        String encryptedResponse = schedulerService.scheduleInvoicePayment(invoiceId, installments, request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }
}