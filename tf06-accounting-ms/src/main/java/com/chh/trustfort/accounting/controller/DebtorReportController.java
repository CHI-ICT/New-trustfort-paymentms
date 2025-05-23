package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.DebtorReportRow;
import com.chh.trustfort.accounting.service.DebtorReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(ApiPath.BASE_API)
@Slf4j
public class DebtorReportController {

    private final DebtorReportService debtorReportService;


    @GetMapping(value = ApiPath.DEBTOR_REPORT)
    public ResponseEntity<List<DebtorReportRow>> generateDebtorReport() {
        List<DebtorReportRow> report = debtorReportService.generateDebtorReport();
        return ResponseEntity.ok(report);
    }
}
