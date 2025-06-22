package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.model.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trustfort/api/v1")
@RequiredArgsConstructor
@Slf4j
public class DecryptionController {

    private final AesService aesService;

    @PostMapping("/decrypt-payload")
    public ResponseEntity<?> decryptPayload(@RequestParam String cipherText) {
        try {
            // 🔐 Create mock AppUser with valid ecred and padding (for test only)
            AppUser appUser = new AppUser();
            appUser.setEcred("3yGm0Iq/BACzr/ueu7wHUgMEFDPW1KBKqM3OR0YJEqss/2HtjIKnRZ9B/FY5jbs9h/jTNlVbM5MBZuri8c41ZjroQ4F/QqVYkkyc01XCDCM=");
            appUser.setPadding("AES/CBC/PKCS5Padding");

            log.info("🔐 Decrypting using AesService...");
            String result = aesService.decrypt(cipherText, appUser);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("❌ Decryption failed", e);
            return ResponseEntity.badRequest().body("❌ Failed to decrypt: " + e.getMessage());
        }
    }
}
