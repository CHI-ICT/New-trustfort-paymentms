package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.repository.AppUserRepository;
import com.chh.trustfort.payment.security.AesService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/utils")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Utility", description = "Internal utilities like encryption and decryption")
public class UtilityController {

    private final AppUserRepository appUserRepository;
    private final AesService aesService;

    @PostMapping(value = "/decrypt-payload", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> decryptPayload(@RequestParam String username, @RequestBody Map<String, String> body) {
        try {
            String encrypted = body.get("cipherText");
            if (encrypted == null || encrypted.isBlank()) {
                return ResponseEntity.badRequest().body("cipherText is required in the request body");
            }

            AppUser appUser = appUserRepository.getAppUserByUserName(username);
            if (appUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("AppUser not found");
            }

            String decrypted = aesService.decrypt(encrypted, appUser);
            return ResponseEntity.ok(Map.of("decrypted", decrypted));
        } catch (Exception ex) {
            log.error("Decryption failed", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Decryption failed: " + ex.getMessage());
        }
    }
}
