package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.DebtorReportDTO;
import com.chh.trustfort.accounting.dto.DebtorReportRow;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.service.DebtorAuditReportService;
import com.chh.trustfort.accounting.service.DebtorReportService;
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
@RequiredArgsConstructor
@Tag(name = "Debtor Reports", description = "Generate summary or detailed audit debtor reports")
@Slf4j
public class DebtorReportController {

    private final DebtorReportService debtorReportService;
    private final DebtorAuditReportService reportService;
    private final RequestManager requestManager;

    @GetMapping(value = ApiPath.DEBTOR_REPORT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateDebtorReport(
            @RequestParam String idToken,
            HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.VIEW_DEBTOR_REPORT.getValue(), "", httpRequest, idToken
        );

        request.appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unauthorized access");
        }

        String result = reportService.generateDebtorAuditReport(request.appUser);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = ApiPath.OVERDUE_DEBTORS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateOverdueDebtorsReport(
            @RequestParam String idToken,
            HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.VIEW_DEBTOR_REPORT.getValue(), "", httpRequest, idToken
        );

        request.appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unauthorized access");
        }

        String result = debtorReportService.generateDebtorReport(request.appUser);
        return ResponseEntity.ok(result);
    }
}

