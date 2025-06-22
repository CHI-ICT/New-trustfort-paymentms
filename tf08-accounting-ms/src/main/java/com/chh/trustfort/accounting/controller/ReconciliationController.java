package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ReconciliationSummaryDTO;
import com.chh.trustfort.accounting.service.ReconciliationEngine;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(ApiPath.BASE_API)
@Slf4j
public class ReconciliationController {

    private final ReconciliationEngine reconciliationEngine;


    @GetMapping(value = ApiPath.GET_RECONCILIATION)
    public ResponseEntity<ReconciliationSummaryDTO> runReconciliation() {
        log.info("Starting reconciliation between receipts and receivables...");
        ReconciliationSummaryDTO summary = reconciliationEngine.reconcileReceivables();
        return ResponseEntity.ok(summary);
    }

}
