package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ApiResponse;
import com.chh.trustfort.accounting.dto.EOPRequestDTO;
import com.chh.trustfort.accounting.dto.EOPResponseDTO;
import com.chh.trustfort.accounting.service.EOPService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// EOPController.java
@RestController
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Evidence of Payment", description = "Generate and retrieve EOP")
public class EOPController {

    private final EOPService eopService;

    @PostMapping("/payables/{invoiceId}/generate-eop")
    public ResponseEntity<ApiResponse> generate(@PathVariable Long invoiceId, @RequestBody EOPRequestDTO request) {
        try {
            EOPResponseDTO eop = eopService.generateEOP(invoiceId, request);
            return ResponseEntity.ok(ApiResponse.success("EOP generated", eop));
        } catch (Exception e) {
            log.error("Failed to generate EOP", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("EOP generation failed: " + e.getMessage()));
        }
    }

    @GetMapping("/payables/{invoiceId}/eop")
    public ResponseEntity<ApiResponse> get(@PathVariable Long invoiceId) {
        return eopService.getEOPByInvoiceId(invoiceId)
                .map(eop -> ResponseEntity.ok(ApiResponse.success("EOP fetched", eop)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("No EOP found for invoice.")));
    }
}