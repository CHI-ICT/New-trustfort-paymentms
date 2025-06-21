package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.service.ReconciliationRetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPath.BASE_API + ApiPath.RECONCILIATION_BASE)
@RequiredArgsConstructor
@Slf4j
public class ReconciliationController {

    private final ReconciliationRetryService reconciliationRetryService;


    @PostMapping(ApiPath.RETRY_FAILED_TRANSFERS)
    public ResponseEntity<String> manuallyTriggerReconciliation() {
        log.info("🔧 Manual trigger of reconciliation job called via API.");
        reconciliationRetryService.retryFailedWithdrawals();
        return ResponseEntity.ok("Reconciliation process executed successfully.");
    }

}
