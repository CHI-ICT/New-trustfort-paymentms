package com.chh.trustfort.accounting.security;

import com.chh.trustfort.accounting.model.AppUser;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author dofoleta
 */
@Service
public class AesServiceImpl implements AesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AesServiceImpl.class);

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
                ivBytes = Arrays.copyOf(ivBytes, 16);
            }
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);

            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            // Use URL-safe Base64 encoder so that the output is safe for URLs (i.e. '-' instead of '+', '_' instead of '/')
            return Base64.getUrlEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            LOGGER.error("Encryption failed: {}", e.getMessage());
            throw new RuntimeException("Encryption failed", e);
        }
    }

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
                ivBytes = Arrays.copyOf(ivBytes, 16);
            }
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

            // Use URL-safe Base64 decoder
            byte[] decodedBytes;
            try {
                decodedBytes = Base64.getUrlDecoder().decode(cipherText);
            } catch (IllegalArgumentException e) {
                LOGGER.error("Base64 Decoding Error: Invalid input. {}", e.getMessage());
                throw new RuntimeException("Base64 Decoding Error: Invalid input.", e);
            }

            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error("Decryption failed: {}", e.getMessage());
            throw new RuntimeException("Decryption failed", e);
        }

   @Override
    public String encrypt(String plaintext, AppUser appUser)  {
        try {
            String secret = appUser.getEcred().split("/")[0];
            String iv = appUser.getEcred().split("/")[1];
            String padding = appUser.getPadding();
            byte[] key = secret.getBytes("UTF-8");
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance(padding);
            
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(iv));
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            
            //Perform Encryption
            return Base64.getEncoder().encodeToString(cipher.doFinal(plaintext.getBytes("UTF-8")));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(AesServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String decrypt(String cipherText, AppUser appUser) {
        try {
            String secret = appUser.getEcred().split("/")[0];
            String iv = appUser.getEcred().split("/")[1];
            String padding = appUser.getPadding();
            byte[] key = secret.getBytes("UTF-8");
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            
            Cipher cipher = Cipher.getInstance(padding);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(iv));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            
            //Perform Decryption
            return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException ex) {
            Logger.getLogger(AesServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

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
