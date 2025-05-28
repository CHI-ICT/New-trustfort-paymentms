package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.model.PaymentSchedule;
import com.chh.trustfort.accounting.service.PaymentSchedulerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payment Scheduling", description = "Handles creation of payment schedules")
public class PaymentScheduleController {

    private final PaymentSchedulerService schedulerService;

    @PostMapping(value = ApiPath.SCHEDULE_PAYMENTS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> schedulePayments(
            @PathVariable Long invoiceId,
            @RequestParam(defaultValue = "2") int installments
    ) {
        try {
            List<PaymentSchedule> schedules = schedulerService.scheduleInvoicePayment(invoiceId, installments);
            return ResponseEntity.status(HttpStatus.CREATED).body(schedules);
        } catch (IllegalStateException ex) {
            log.warn("Warning: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (Exception ex) {
            log.error("Error generating schedules: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating schedules: " + ex.getMessage());
        }
    }
}