package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.CreateChartOfAccountRequest;
import com.chh.trustfort.accounting.service.ChartOfAccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
@Tag(name = "Chart of Account", description = "Handles Chart of Account operations")
@SecurityRequirement(name = "bearerAuth")
public class ChartOfAccountController {

    private final ChartOfAccountService chartOfAccountService;


    @PostMapping(value = ApiPath.CREATE_CHART_OF_ACCOUNT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAccount(@RequestBody CreateChartOfAccountRequest request) {
        chartOfAccountService.createAccount(request);
        return ResponseEntity.ok("Account created successfully.");
    }
}
