package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.CashFlowStatementDTO;
import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.service.CashFlowStatementService;
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
@Tag(name = "Journal Entry", description = "Handles posting of journal entries")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CashFlowStatementController {

    private final CashFlowStatementService cashFlowStatementService;

    @GetMapping(value = ApiPath.CASH_FLOW_STATEMENT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CashFlowStatementDTO> getCashFlowStatement(StatementFilterDTO filter)
     {
        CashFlowStatementDTO result = cashFlowStatementService.generateCashFlowStatement(filter);
        return ResponseEntity.ok(result);
    }
}


