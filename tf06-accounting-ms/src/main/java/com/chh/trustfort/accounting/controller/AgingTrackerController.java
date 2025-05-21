package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.DebtAgingSummaryRow;
import com.chh.trustfort.accounting.service.AgingTrackerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(ApiPath.BASE_API)
@Slf4j
public class AgingTrackerController {

    private final AgingTrackerService agingTrackerService;


    @GetMapping(value = ApiPath.DEBT_AGING_SUMMARY)
    public ResponseEntity<List<DebtAgingSummaryRow>> getDebtAgingSummary() {
        List<DebtAgingSummaryRow> report = agingTrackerService.generateAgingSummary();
        return ResponseEntity.ok(report);
    }
}
