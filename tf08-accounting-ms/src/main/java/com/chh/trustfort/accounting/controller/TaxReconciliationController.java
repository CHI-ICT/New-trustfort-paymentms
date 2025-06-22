package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ReconciliationResultDTO;
import com.chh.trustfort.accounting.service.TaxReconciliationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(ApiPath.BASE_API)
@Tag(name = "Tax Reconciliation", description = "Handles Tax Posting Reconciliation")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TaxReconciliationController {

    private final TaxReconciliationService taxReconciliationService;

    @GetMapping(ApiPath.RECONCILE_TAX)
    @Operation(summary = "Reconcile Tax Postings for a Date Range")
    public ResponseEntity<List<ReconciliationResultDTO>> reconcileTaxes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<ReconciliationResultDTO> results = taxReconciliationService.reconcileTaxPostings(startDate, endDate);
        return ResponseEntity.ok(results);
    }
}
