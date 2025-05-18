package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.EquityStatementResponse;
import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.service.EquityStatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@Slf4j
@RequestMapping(ApiPath.BASE_API)
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Equity Statement", description = "APIs for generating Statement of Equity reports")
public class EquityStatementController {

    private final EquityStatementService equityStatementService;

    @GetMapping(value = ApiPath.EQUITY_STATEMENT)
    @Operation(
            summary = "Generate Statement of Equity",
            description = "Returns equity report based on retained earnings, contributions, dividends, and net change."
    )
    public ResponseEntity<EquityStatementResponse> getEquityStatement(@RequestBody StatementFilterDTO filter) {
        return ResponseEntity.ok(equityStatementService.generateStatement(filter));
    }
}


