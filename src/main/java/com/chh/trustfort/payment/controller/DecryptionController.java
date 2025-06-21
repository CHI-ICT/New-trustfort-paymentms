package com.chh.trustfort.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trustfort/api/v1")
@Slf4j
public class DecryptionController {

    @PostMapping("/decrypt-payload")
    public ResponseEntity<?> decryptPayload(@RequestParam String cipherText) {
        try {
            // Hardcoded from DB
            String encryptionKey = "hNOJC7azs86txXCpBCLFmxqehFwKGWuE7h7lKuzvLw94QmnzMvLnEA8hPHSB7afD";
            String ecred = "3yGm0Iq/BACzr/ueu7wHUgMEFDPW1KBKqM3OR0YJEqss/2HtjIKnRZ9B/FY5jbs9h/jTNlVbM5MBZuri8c41ZjroQ4F/QqVYkkyc01XCDCM=";

            // Step 1: Decrypt the ecred using the key
            String decrypted = decryptEcred(ecred, encryptionKey); // returns: AES_KEY/IV_HEX

            String[] parts = decrypted.split("/");
            String aesKey = parts[0];
            String ivHex = parts[1];

            // Step 2: Decrypt the payload
            String plainText = decryptAES(cipherText, aesKey, ivHex);

            return ResponseEntity.ok(plainText);

        } catch (Exception e) {
            log.error("Decryption failed", e);
            return ResponseEntity.badRequest().body("Failed to decrypt: " + e.getMessage());
        }
    }

    private String decryptEcred(String encrypted, String key) throws Exception {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES"); // Must match original algorithm
        encryptor.setPassword(key);
        return encryptor.decrypt(encrypted); // returns "actualKey/ivHex"
    }

    private String decryptAES(String cipherTextBase64, String aesKey, String ivHex) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(cipherTextBase64);
        byte[] keyBytes = aesKey.getBytes(StandardCharsets.UTF_8);
        byte[] ivBytes = hexStringToByteArray(ivHex);

        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] original = cipher.doFinal(decoded);
        return new String(original, StandardCharsets.UTF_8);
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] result = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            result[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return result;
    }
}