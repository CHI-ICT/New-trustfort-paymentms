package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.CreditNoteRequestDTO;
import com.chh.trustfort.accounting.model.CreditNote;
import com.chh.trustfort.accounting.service.CreditNoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.BASE_API)
@Tag(name = "Credit Notes", description = "Handles Credit Note creation and linkage to Debit Notes")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class CreditNoteController {

    private final CreditNoteService creditNoteService;

    @PostMapping(value = ApiPath.CREATE_CREDIT_NOTE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCreditNote(@Valid @RequestBody CreditNoteRequestDTO request) {
        try {
            CreditNote note = creditNoteService.createCreditNote(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(note);
        } catch (Exception ex) {
            log.error("Error creating credit note: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create credit note: " + ex.getMessage());
        }
    }


}
