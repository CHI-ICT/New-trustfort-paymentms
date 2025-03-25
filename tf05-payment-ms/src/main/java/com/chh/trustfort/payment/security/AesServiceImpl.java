package com.chh.trustfort.payment.security;

import com.chh.trustfort.payment.model.AppUser;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AesServiceImpl implements AesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AesServiceImpl.class);

    /**
     * Encrypts the plaintext using AES/CBC/PKCS5Padding.
     * The provided keyWithIv should be in the format "keyPart/ivPart".
     * The keyPart is converted into a 16-byte key by applying MD5,
     * and the ivPart is adjusted to 16 bytes.
     */
    @Override
    public String encrypt(String plaintext, String keyWithIv) {
        if (keyWithIv == null || keyWithIv.trim().isEmpty()) {
            LOGGER.warn("No valid key provided, returning plaintext.");
            return plaintext;
        }
        try {
            String[] parts = keyWithIv.split("/");
            if (parts.length < 2) {
                throw new RuntimeException("Invalid encryption key format for user.");
            }
            String keyPart = parts[0];
            String ivPart = parts[1];

            // Derive a 16-byte AES key using MD5 hash on keyPart
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] keyBytes = md.digest(keyPart.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

            // Process IV: if ivPart appears to be hex (length 32), decode it; otherwise, adjust its UTF-8 bytes to 16 bytes.
            byte[] ivBytes;
            if (ivPart.length() == 32 && ivPart.matches("[0-9a-fA-F]+")) {
                ivBytes = hexStringToByteArray(ivPart);
            } else {
                ivBytes = ivPart.getBytes(StandardCharsets.UTF_8);
                if (ivBytes.length < 16) {
                    ivBytes = Arrays.copyOf(ivBytes, 16);
                } else if (ivBytes.length > 16) {
                    ivBytes = Arrays.copyOf(ivBytes, 16);
                }
            }
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);

            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            LOGGER.error("Encryption failed: {}", e.getMessage());
            throw new RuntimeException("Encryption failed");
        }
    }

    /**
     * Decrypts the cipherText using AES/CBC/PKCS5Padding.
     * The provided keyWithIv is processed in the same way as in encrypt().
     */
    @Override
    public String decrypt(String cipherText, String keyWithIv) {
        if (keyWithIv == null || keyWithIv.trim().isEmpty()) {
            LOGGER.warn("No valid key provided, cannot decrypt. Returning cipherText as-is.");
            return cipherText;
        }
        try {
            String[] parts = keyWithIv.split("/");
            if (parts.length < 2) {
                throw new RuntimeException("Invalid encryption key format for user.");
            }
            String keyPart = parts[0];
            String ivPart = parts[1];

            // Derive a 16-byte AES key using MD5 hash on keyPart
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] keyBytes = md.digest(keyPart.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

            // Process IV similarly to encrypt()
            byte[] ivBytes;
            if (ivPart.length() == 32 && ivPart.matches("[0-9a-fA-F]+")) {
                ivBytes = hexStringToByteArray(ivPart);
            } else {
                ivBytes = ivPart.getBytes(StandardCharsets.UTF_8);
                if (ivBytes.length < 16) {
                    ivBytes = Arrays.copyOf(ivBytes, 16);
                } else if (ivBytes.length > 16) {
                    ivBytes = Arrays.copyOf(ivBytes, 16);
                }
            }
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error("Decryption failed: {}", e.getMessage());
            throw new RuntimeException("Decryption failed");
        }
    }

    /**
     * Converts a hexadecimal string into a byte array.
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
