package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.payload.ReceiptGenerationRequest;
import com.chh.trustfort.accounting.service.ReceiptService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(ApiPath.BASE_API)
@Slf4j
public class ReceiptController {

    private final ReceiptService receiptService;


    @PostMapping(value = ApiPath.GENERATE_RECEIPT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Receipt> generateReceipt(@RequestBody ReceiptGenerationRequest request) {
        Receipt receipt = receiptService.generateReceipt(request);
        return ResponseEntity.ok(receipt);
    }
}
