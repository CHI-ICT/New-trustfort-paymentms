// Controller

package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.*;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.CashflowForecastService;
import com.google.gson.Gson;
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
@Tag(name = "Cashflow Forecast", description = "Predicts cash inflows and outflows")
@Slf4j
public class CashflowForecastController {

    private final CashflowForecastService forecastService;
    private final AesService aesService;
    private final RequestManager requestManager;
    private final Gson gson;

    @PostMapping(value = ApiPath.FORCAST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> forecast(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        log.info("📥 Received forecast generation request");

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.CASHFLOW_FORECAST.getValue(), requestPayload, httpRequest, idToken
        );

        AppUser appUser = request.appUser;
        appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            log.warn("❌ Request validation failed: {}", request.payload);
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.ok(
                    aesService.encrypt(SecureResponseUtil.error(
                            error.getResponseCode(), error.getResponseMessage(), String.valueOf(HttpStatus.UNAUTHORIZED)), appUser)
            );
        }

        CashflowForecastRequest filter = gson.fromJson(request.payload, CashflowForecastRequest.class);
        List<CashflowForecastResponse> forecast = forecastService.generateForecast(filter);

        ApiResponse response = ApiResponse.error(ApiResponse.success("Forecast generated successfully", forecast));
        return ResponseEntity.ok(aesService.encrypt(String.valueOf(response), appUser));
    }
}
