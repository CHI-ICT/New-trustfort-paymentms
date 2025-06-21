package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.payload.TokenData;
import com.chh.trustfort.payment.repository.AppUserRepository;
import com.chh.trustfort.payment.security.AesService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
@RestController
@RequestMapping("/trustfort/api/v1")
@RequiredArgsConstructor
@Slf4j
public class DebugController {

    private final AesService aesService;
    private final AppUserRepository appUserRepository;
    private final StringEncryptor stringEncryptor;

    @GetMapping("/decrypt-payload")
    public ResponseEntity<?> decryptPayload(
            @RequestParam("idToken") String idToken,
            @RequestParam("encryptedText") String encryptedText
    ) {
        try {
            // Step 1: Decode token and extract username (sub)
            String[] pieces = idToken.replace("Bearer ", "").split("\\.");
            String b64Payload = pieces[1];
            String json = new String(Base64.getDecoder().decode(b64Payload), StandardCharsets.UTF_8);
            TokenData tokenData = new Gson().fromJson(json, TokenData.class);
            String username = tokenData.getSub();

            // Step 2: Get AppUser
            AppUser appUser = appUserRepository.getAppUserByUserName(username);
            if (appUser == null) {
                return ResponseEntity.badRequest().body("Invalid user");
            }

            // Step 3: Decrypt encrypted text
            String decrypted = aesService.decrypt(encryptedText, appUser);
            return ResponseEntity.ok(decrypted);

        } catch (Exception ex) {
            log.error("Decryption failed", ex);
            return ResponseEntity.badRequest().body("Invalid token or payload");
        }
    }
}
