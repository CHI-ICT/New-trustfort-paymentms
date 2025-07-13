package com.chh.trustfort.accounting.controller.statements;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.DoubleEntryRequest;
import com.chh.trustfort.accounting.dto.JournalEntryRequest;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.JournalEntryService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@Tag(name = "Journal Entry", description = "Handles posting of journal entries")
@RequiredArgsConstructor
public class JournalEntryController {

    private final JournalEntryService journalEntryService;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;



    @PostMapping(value = ApiPath.INTERNAL_POST_JOURNAL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postJournalEntryInternal(@RequestBody JournalEntryRequest dto) {
        log.info("📥 Received internal journal entry post from Feign client: {}", dto.getReference());

        // 🧪 Call with null user (so plain JSON is returned as String)
        String responseJson = journalEntryService.createdJournalEntry(dto, null);

        // ✅ Parse the string back to an object before returning
        Object parsedJson = new Gson().fromJson(responseJson, Object.class);

        return ResponseEntity.ok(parsedJson);
    }



    @PostMapping(value = ApiPath.JOURNAL_ENTRY, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postJournalEntry(
            @RequestParam String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.JOURNAL_ENTRY.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload errorResponse = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(aesService.encrypt(gson.toJson(errorResponse), null));
        }

        JournalEntryRequest dto = gson.fromJson(request.payload, JournalEntryRequest.class);
        String encryptedResponse = journalEntryService.createJournalEntry(dto, request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }


    @PostMapping(value = ApiPath.DOUBLE_ENTRY, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postDoubleEntry(
            @RequestParam(required = false) String idToken,  // ✅ optional now
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        // Let the RequestManager handle token extraction from header if idToken is null
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.DOWNLOAD_REPORT.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload errorResponse = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(aesService.encrypt(gson.toJson(errorResponse), null));
        }

        DoubleEntryRequest dto = gson.fromJson(request.payload, DoubleEntryRequest.class);
        String encryptedResponse = journalEntryService.recordDoubleEntry(dto, request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }

}