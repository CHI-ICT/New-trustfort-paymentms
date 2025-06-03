package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ApiResponse;
import com.chh.trustfort.accounting.model.Contract;
import com.chh.trustfort.accounting.model.PurchaseOrder;
import com.chh.trustfort.accounting.repository.ContractRepository;
import com.chh.trustfort.accounting.repository.PurchaseOrderRepository;
import com.chh.trustfort.accounting.service.ContractService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@EncryptResponse
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
@Tag(name = "Contract", description = "Manage Contracts")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class ContractController {

    private final ContractService contractService;

    @PostMapping(value = ApiPath.CREATE_CONTRACT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createContract(@RequestBody Contract request) {
        try {
            Contract contract = contractService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.builder()
                            .status("success")
                            .message("Contract created successfully")
                            .data(contract)
                            .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.builder()
                            .status("error")
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Unexpected error during contract creation", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.builder()
                            .status("error")
                            .message("An unexpected error occurred")
                            .build());
        }
    }

    @GetMapping(ApiPath.ALL_CONTRACT)
    public ResponseEntity<?> getAllContracts() {
        return ResponseEntity.ok(contractService.getAll());
    }
}
