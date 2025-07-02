// ==== CONTROLLER: ChartOfAccountController.java ====
package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ApiResponse;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import com.chh.trustfort.accounting.payload.CreateCOARequestPayload;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.ChartOfAccountService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static com.chh.trustfort.accounting.constant.ApiPath.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chart of Account", description = "Manage chart of account creation and retrieval")
public class ChartOfAccountController {

    private final ChartOfAccountService chartOfAccountService;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;

    @PostMapping(value = ApiPath.CREATE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createChartOfAccount(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        log.info("📝 Received request to create chart of account");

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.CREATE_COA.getValue(), requestPayload, httpRequest, idToken
        );

        AppUser appUser = request.appUser;
        appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.ok(aesService.encrypt(SecureResponseUtil.error(
                    error.getResponseCode(), error.getResponseMessage(), String.valueOf(HttpStatus.UNAUTHORIZED)
            ), appUser));
        }

        CreateCOARequestPayload dto = gson.fromJson(request.payload, CreateCOARequestPayload.class);
        ChartOfAccount result = chartOfAccountService.createAccount(dto);
        ApiResponse response = ApiResponse.error(ApiResponse.success("Account created successfully", result));
        return ResponseEntity.ok(aesService.encrypt(String.valueOf(response), appUser));
    }

    @GetMapping(value = ApiPath.GET_ALL)
    public ResponseEntity<String> getAllChartOfAccounts(
            @RequestParam String idToken,
            HttpServletRequest httpRequest
    ) {
        log.info("📚 Request to fetch all chart of accounts");

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.VIEW_COA.getValue(), null, httpRequest, idToken
        );

        AppUser appUser = request.appUser;
        appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            return ResponseEntity.ok(
                    aesService.encrypt(SecureResponseUtil.error("06", "Unauthorized", "401"), appUser)
            );
        }

        List<ChartOfAccount> results = chartOfAccountService.findAll();
        return ResponseEntity.ok(
                aesService.encrypt(ApiResponse.success("Accounts retrieved successfully", results), appUser)
        );
    }
}


