// Controller

package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.*;
import com.chh.trustfort.accounting.service.CashflowForecastService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@EncryptResponse
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Cashflow Forecast", description = "Predicts cash inflows and outflows")
public class CashflowForecastController {

    private final CashflowForecastService forecastService;


    @PostMapping(value = ApiPath.FORCAST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> forecast(@RequestBody CashflowForecastRequest request) {
        log.info("Generating forecast from {} to {}", request.getStartDate(), request.getEndDate());
        List<CashflowForecastResponse> forecast = forecastService.generateForecast(request);
        return ResponseEntity.ok(ApiResponse.success("Forecast generated successfully", forecast));
    }
}