// --- Controller ---

package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.model.DebitNote;
import com.chh.trustfort.accounting.service.DebitNoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.BASE_API)
@Tag(name = "Debit Notes", description = "Link old and new debit notes")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class DebitNoteController {

    private final DebitNoteService debitNoteService;


    @PostMapping(value = ApiPath.CREATE_DEBIT_NOTE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> replaceDebitNote(
            @PathVariable Long oldNoteId,
            @RequestBody DebitNote newNotePayload,
            @RequestParam String createdBy
    ) {
        try {
            DebitNote result = debitNoteService.linkNewDebitNoteToOld(oldNoteId, newNotePayload, createdBy);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            log.error("Error linking new debit note: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to link debit note: " + e.getMessage());
        }
    }
}
