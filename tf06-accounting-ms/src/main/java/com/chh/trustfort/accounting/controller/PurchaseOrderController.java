package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ApiResponse;
import com.chh.trustfort.accounting.model.PurchaseOrder;
import com.chh.trustfort.accounting.repository.PurchaseOrderRepository;
import com.chh.trustfort.accounting.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
@EncryptResponse
@Tag(name = "Purchase Order", description = "Manage Purchase Orders")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping(value = ApiPath.CREATE_PO, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPO(@RequestBody PurchaseOrder request) {
        try {
            PurchaseOrder saved = purchaseOrderService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.builder()
                            .status("success")
                            .message("Purchase order created successfully")
                            .data(saved)
                            .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.builder()
                            .status("error")
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Unexpected error during PO creation", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.builder()
                            .status("error")
                            .message("An unexpected error occurred")
                            .build());
        }
    }

    @GetMapping(ApiPath.ALL_PO)
    public ResponseEntity<?> getAllPOs() {
        return ResponseEntity.ok(purchaseOrderService.getAll());
    }
}
