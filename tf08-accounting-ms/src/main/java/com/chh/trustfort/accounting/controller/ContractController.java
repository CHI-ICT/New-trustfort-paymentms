package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ApiResponse;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.Contract;
import com.chh.trustfort.accounting.model.PurchaseOrder;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.repository.ContractRepository;
import com.chh.trustfort.accounting.repository.PurchaseOrderRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.ContractService;
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
@Slf4j
@Tag(name = "Contract", description = "Manage Contracts and View Existing Ones")
public class ContractController {

    private final ContractService contractService;
    private final AesService aesService;
    private final RequestManager requestManager;
    private final Gson gson;

    @PostMapping(value = ApiPath.CREATE_CONTRACT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createContract(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        log.info("📨 Incoming contract creation request");

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.CREATE_CONTRACT.getValue(), requestPayload, httpRequest, idToken
        );

        AppUser appUser = request.appUser;
        appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.ok(aesService.encrypt(SecureResponseUtil.error(
                    error.getResponseCode(), error.getResponseMessage(), String.valueOf(HttpStatus.UNAUTHORIZED)
            ), appUser));
        }

        try {
            Contract dto = gson.fromJson(request.payload, Contract.class);
            Contract saved = contractService.create(dto);
            return ResponseEntity.ok(aesService.encrypt(
                    ApiResponse.success("Contract created successfully", saved), appUser
            ));
        } catch (RuntimeException e) {
            log.warn("⚠️ Contract creation failed: {}", e.getMessage());
            return ResponseEntity.ok(aesService.encrypt(
                    String.valueOf(ApiResponse.error( e.getMessage())), appUser
            ));
        } catch (Exception ex) {
            log.error("❌ Unexpected error during contract creation", ex);
            return ResponseEntity.ok(aesService.encrypt(
                    String.valueOf(ApiResponse.error( "An unexpected error occurred")), appUser
            ));
        }
    }

    @GetMapping(value = ApiPath.ALL_CONTRACT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllContracts(
            @RequestParam String idToken,
            HttpServletRequest httpRequest
    ) {
        log.info("📦 Request to fetch all contracts");

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.VIEW_CONTRACT.getValue(), null, httpRequest, idToken
        );

        AppUser appUser = request.appUser;
        appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            return ResponseEntity.ok(
                    aesService.encrypt(SecureResponseUtil.error("06", "Unauthorized access", "401"), appUser)
            );
        }

        List<Contract> contracts = contractService.getAll();
        return ResponseEntity.ok(
                aesService.encrypt(ApiResponse.success("Contracts retrieved successfully", contracts), appUser)
        );
    }
}
