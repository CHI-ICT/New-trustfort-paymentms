package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;

import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.IncomeStatementResponse;
import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.service.EquityStatementService;
import com.chh.trustfort.accounting.service.IncomeStatementService;
import com.chh.trustfort.accounting.payload.DateRangeRequest;
import com.chh.trustfort.accounting.model.Users;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@RestController
@Tag(name = "Financial Reports", description = "Handles Income Statement Report Generation")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(ApiPath.BASE_API)
@Slf4j
@RequiredArgsConstructor
public class IncomeStatementController {

    private final IncomeStatementService incomeStatementService;
    private final RequestManager requestManager;
    private final Gson gson;



    @PostMapping(value = ApiPath.GENERATE_INCOME_STATEMENT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> generateIncomeStatement(@RequestBody DateRangeRequest payload, HttpServletRequest httpRequest) {

        // Validate the request
        Quintuple<Boolean, String, String, Users, String> validationResult = requestManager.validateRequest(
                Role.FETCH_WALLET.getValue(), gson.toJson(payload), httpRequest, ApiPath.ID_TOKEN
        );

        if (validationResult.isError) {
            return new ResponseEntity<>(validationResult.payload, HttpStatus.OK);
        }

        Users user = validationResult.Users;

        if (user == null) {
            log.error("Request validation failed: User is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access: User not found");
        }

        DateRangeRequest parsedPayload = gson.fromJson(validationResult.payload, DateRangeRequest.class);

        // ✅ Convert to StatementFilterDTO
        StatementFilterDTO filter = new StatementFilterDTO();
        filter.setStartDate(parsedPayload.getStartDate());
        filter.setEndDate(parsedPayload.getEndDate());

        IncomeStatementResponse response = incomeStatementService.generateIncomeStatement(filter);
        return ResponseEntity.ok(response);
    }


//    @GetMapping(value = ApiPath.GET_INCOME_STATEMENT)
//    public ResponseEntity<IncomeStatementResponse> getIncomeStatement(@RequestBody StatementFilterDTO filters) {
//        return ResponseEntity.ok(incomeStatementService.generateIncomeStatement(filters));
//    }

    @PostMapping(value = ApiPath.GET_INCOME_STATEMENT)
    public ResponseEntity<IncomeStatementResponse> getIncomeStatement(@RequestBody StatementFilterDTO filters) {
        return ResponseEntity.ok(incomeStatementService.generateIncomeStatement(filters));
    }

}


