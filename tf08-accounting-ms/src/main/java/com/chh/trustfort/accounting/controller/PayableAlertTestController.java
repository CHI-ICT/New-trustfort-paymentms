package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.service.PayableAlertServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
@Tag(name = "Payable Alerts", description = "Trigger alerts for due or overdue invoices")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class PayableAlertTestController {

    private final PayableAlertServiceImpl payableAlertService;

    @GetMapping(ApiPath.TEST_ALERTS)
    public ResponseEntity<?> testAlerts() {
        List<String> alerts = payableAlertService.generateAlerts();
        if (alerts.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No alerts found", "alerts", alerts));
        }
        return ResponseEntity.ok(Map.of("message", "Alerts generated", "alerts", alerts));
    }

}
