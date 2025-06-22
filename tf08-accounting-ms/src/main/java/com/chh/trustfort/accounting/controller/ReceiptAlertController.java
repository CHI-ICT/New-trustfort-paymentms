package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.service.ReceiptAlertService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(ApiPath.BASE_API)
@Slf4j
@Tag(name = "Receipt Alerts", description = "Alert for Pending Receipts")
public class ReceiptAlertController {

    private final ReceiptAlertService receiptAlertService;

    @GetMapping(ApiPath.ALERT_PENDING_RECEIPTS)
    public ResponseEntity<List<Receipt>> getPendingReceipts() {
        List<Receipt> alerts = receiptAlertService.getPendingReceipts();
        return ResponseEntity.ok(alerts);
    }
}
