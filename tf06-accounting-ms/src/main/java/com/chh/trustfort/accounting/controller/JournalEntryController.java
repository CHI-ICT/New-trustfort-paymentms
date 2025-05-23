package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ApiResponse;
import com.chh.trustfort.accounting.dto.DoubleEntryRequest;
import com.chh.trustfort.accounting.dto.JournalEntryRequest;
import com.chh.trustfort.accounting.service.JournalEntryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping(ApiPath.BASE_API)
@Tag(name = "Journal Entry", description = "Handles posting of journal entries")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    @PostMapping(value = ApiPath.JOURNAL_ENTRY, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postJournalEntry(@RequestBody JournalEntryRequest request) {
        log.info("📥 Single journal entry request received: {}", request);
        journalEntryService.createJournalEntry(request);
        return ResponseEntity.ok(new ApiResponse("Journal entry recorded successfully."));
    }

    @PostMapping(value = ApiPath.DOUBLE_ENTRY, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postDoubleEntry(@RequestBody DoubleEntryRequest request) {
        log.info("📥 Double entry request: DR [{}], CR [{}], Amount: {}", request.getDebitAccountCode(), request.getCreditAccountCode(), request.getAmount());

        journalEntryService.recordDoubleEntry(
                request.getDebitAccountCode(),
                request.getCreditAccountCode(),
                request.getReference(),
                request.getDescription(),
                request.getAmount(),
                request.getDepartment(),
                request.getBusinessUnit(),
                request.getTransactionDate()
        );

        return ResponseEntity.ok(new ApiResponse("Double-entry journal successfully recorded."));
    }
}


