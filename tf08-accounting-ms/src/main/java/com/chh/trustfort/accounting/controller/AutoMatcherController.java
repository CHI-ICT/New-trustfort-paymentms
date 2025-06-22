package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.MatchedPairDTO;
import com.chh.trustfort.accounting.dto.MatchingResponseDTO;
import com.chh.trustfort.accounting.enums.MatchingStatus;
import com.chh.trustfort.accounting.enums.ReceiptStatus;
import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.model.Receivable;
import com.chh.trustfort.accounting.repository.ReceiptRepository;
import com.chh.trustfort.accounting.repository.ReceivableRepository;
import com.chh.trustfort.accounting.service.AutoMatcherService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.BASE_API)
@Tag(name = "Auto-Matching", description = "Match receipts to receivables")
@SecurityRequirement(name = "bearerAuth")
public class AutoMatcherController {

    private final AutoMatcherService autoMatcherService;
    private final ReceiptRepository receiptRepository;
    private final ReceivableRepository receivableRepository;


    @PostMapping(value = ApiPath.MATCHER, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> match(@RequestParam String payerEmail) {
        List<Receipt> receipts = receiptRepository.findByPayerEmailAndStatus(payerEmail, ReceiptStatus.CONFIRMED);
        List<MatchedPairDTO> matchedPairs = autoMatcherService.matchOpenReceivablesFor(payerEmail);

        MatchingResponseDTO response = new MatchingResponseDTO(
                "success",
                "Matching completed successfully.",
                payerEmail,
                receipts.size(),
                matchedPairs.size(),
                matchedPairs,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }
}