// --- Controller ---

package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ReversalRequestDTO;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.DebitNote;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.DebitNoteService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Tag(name = "Debit Notes", description = "Manage Debit Notes and reversals")
@Slf4j
public class DebitNoteController {

    private final DebitNoteService debitNoteService;
    private final RequestManager requestManager;
    private final Gson gson;
    private final AesService aesService;

    @PostMapping(value = ApiPath.REVERSE_DEBIT_NOTE, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> reverseDebitNote(
            @RequestHeader("Authorization") String auth,
            @RequestBody String encryptedPayload,
            HttpServletRequest httpRequest) {

        String idToken = auth.replace("Bearer ", "").trim();
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.REVERSE_DEBIT_NOTE.getValue(), encryptedPayload, httpRequest, idToken
        );
        request.appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            OmniResponsePayload error = gson.fromJson(aesService.decrypt(request.payload, request.appUser), OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(error.getResponseCode(), error.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        log.info("📥 Decrypted Payload: {}", request.payload);
        ReversalRequestDTO payload = gson.fromJson(request.payload, ReversalRequestDTO.class);
        String result = debitNoteService.reverseDebitNote(payload, request.appUser);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = ApiPath.CREATE_DEBIT_NOTE, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> linkNewDebitNote(
            @RequestHeader("Authorization") String auth,
            @RequestBody String encryptedPayload,
            HttpServletRequest httpRequest) {

        String idToken = auth.replace("Bearer ", "").trim();
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.CREATE_DEBIT_NOTE.getValue(), encryptedPayload, httpRequest, idToken
        );
        request.appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            OmniResponsePayload error = gson.fromJson(aesService.decrypt(request.payload, request.appUser), OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(error.getResponseCode(), error.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        log.info("📥 Decrypted Payload: {}", request.payload);
        JsonObject json = JsonParser.parseString(request.payload).getAsJsonObject();
        Long oldNoteId = json.get("oldNoteId").getAsLong();
        String createdBy = json.get("createdBy").getAsString();
        DebitNote newNote = gson.fromJson(json.get("newNote").toString(), DebitNote.class);

        String result = debitNoteService.linkNewDebitNoteToOld(oldNoteId, newNote, createdBy, request.appUser);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
