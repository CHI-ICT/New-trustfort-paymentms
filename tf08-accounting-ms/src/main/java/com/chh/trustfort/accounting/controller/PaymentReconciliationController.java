package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ApiResponse;
import com.chh.trustfort.accounting.service.VendorPaymentReconciler;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@EncryptResponse
@RequiredArgsConstructor
@RequestMapping(ApiPath.BASE_API)
@Tag(name = "Vendor Payment Reconciliation", description = "Reconcile vendor invoices with bank transactions")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class PaymentReconciliationController {

    private final VendorPaymentReconciler reconciler;


    @PostMapping(value = ApiPath.TRIGGER_RECONCILIATION, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> triggerReconciliation() {
        var result = reconciler.reconcileVendorPayments();
        return ResponseEntity.ok(ApiResponse.success("Reconciliation completed", result));
    }
}
