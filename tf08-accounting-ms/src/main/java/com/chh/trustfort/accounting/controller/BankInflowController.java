package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.BankInflowPayload;
import com.chh.trustfort.accounting.service.BankInflowSyncService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Bank Inflow", description = "Simulate and Sync Bank Inflows")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(ApiPath.BASE_API)
@Slf4j
public class BankInflowController {

    private final BankInflowSyncService bankInflowSyncService;


    @PostMapping(value = ApiPath.SYNC_BANK_INFLOW, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> syncBankInflow(@RequestBody BankInflowPayload payload) {
        String result = bankInflowSyncService.syncInflow(payload);
        return ResponseEntity.ok(result);
    }
}
