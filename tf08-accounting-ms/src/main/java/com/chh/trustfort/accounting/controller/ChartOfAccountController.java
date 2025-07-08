// ==== CONTROLLER: ChartOfAccountController.java ====
package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;

import com.chh.trustfort.accounting.Responses.ApiResponse;
import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.Utility.LocalDateTimeTypeAdapter;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;

import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import com.chh.trustfort.accounting.payload.CreateCOARequestPayload;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.ChartOfAccountService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
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

    @PostMapping(value = ApiPath.CREATE, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createChartOfAccount(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        String idToken = authorizationHeader.replace("Bearer ", "").trim();
        log.info("🔐 ID TOKEN: {}", idToken);
        log.info("📥 RAW ENCRYPTED PAYLOAD: {}", requestPayload);

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.CREATE_COA.getValue(), requestPayload, httpRequest, idToken
        );

        AppUser appUser = request.appUser;
        appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            String decryptedError = aesService.decrypt(request.payload, appUser);
            OmniResponsePayload error = gson.fromJson(decryptedError, OmniResponsePayload.class);
            String encryptedError = aesService.encrypt(SecureResponseUtil.error(
                    error.getResponseCode(), error.getResponseMessage(), String.valueOf(HttpStatus.UNAUTHORIZED)
            ), appUser);
            return ResponseEntity.ok(encryptedError);
        }

        log.info("📥 Decrypted Payload: {}", request.payload);
        CreateCOARequestPayload dto = new Gson().fromJson(request.payload, CreateCOARequestPayload.class);

        ChartOfAccount result = chartOfAccountService.createAccount(dto);
        ApiResponse response = ApiResponse.success("Account created successfully", result, "00", "SUCCESS");

        Gson customGson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();

        return ResponseEntity.ok(aesService.encrypt(customGson.toJson(response), appUser));
    }

//    @GetMapping(value = ApiPath.GET_ALL)
//    public ResponseEntity<String> getAllChartOfAccounts(
//            @RequestParam String idToken,
//            HttpServletRequest httpRequest
//    ) {
//        log.info("📚 Request to fetch all chart of accounts");
//
//        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
//                Role.VIEW_COA.getValue(), null, httpRequest, idToken
//        );
//
//        AppUser appUser = request.appUser;
//        appUser.setIpAddress(httpRequest.getRemoteAddr());
//
//        if (request.isError) {
//            return ResponseEntity.ok(
//                    aesService.encrypt(SecureResponseUtil.error("06", "Unauthorized", "401"), appUser)
//            );
//        }
//
//        List<ChartOfAccount> results = chartOfAccountService.findAll();
//        return ResponseEntity.ok(
//                aesService.encrypt(String.valueOf(ApiResponse.success("Accounts retrieved successfully", results)), appUser)
//        );
//    }
}


