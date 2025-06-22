package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.BalanceSheetFilterRequest;
import com.chh.trustfort.accounting.dto.IntegrityCheckResult;
import com.chh.trustfort.accounting.service.StatementIntegrityService;
import io.swagger.v3.oas.annotations.Operation;
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

import java.util.List;


@RestController
@Tag(name = "Financial Reports", description = "Handles Statement integrity validation")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(ApiPath.BASE_API)
@Slf4j
@RequiredArgsConstructor
public class StatementIntegrityController {


    private StatementIntegrityService integrityService;


    @PostMapping(value = ApiPath.VALIDATE_STATEMENT_INTEGRITY, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Validate Statement Integrity Rules")
    public ResponseEntity<List<IntegrityCheckResult>> validateStatementIntegrity(@RequestBody BalanceSheetFilterRequest filter) {
        return ResponseEntity.ok(integrityService.validateAllStatements(filter));
    }

}


