package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ApiResponse;
import com.chh.trustfort.accounting.service.GLAutoPosterService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@EncryptResponse
@RequiredArgsConstructor
@RequestMapping(ApiPath.BASE_API)
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payables GL Posting", description = "Post approved invoices to GL")
public class GLPostingController {

    private final GLAutoPosterService glAutoPosterService;


    @PostMapping(value = ApiPath.POST_TO_GL, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> postToGL(@PathVariable Long invoiceId) {
        log.info("Received request to post Payable Invoice [{}] to GL", invoiceId);
        try {
            ApiResponse response = glAutoPosterService.postInvoiceToGL(invoiceId);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            log.error("❌ Failed to post invoice [{}] to GL: {}", invoiceId, ex.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.builder()
                            .status("error")
                            .message("Posting to GL failed: " + ex.getMessage())
                            .data(null)
                            .build()
            );
        }
    }
} 
