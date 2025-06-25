package com.chh.trustfort.accounting.security;

import com.chh.trustfort.accounting.model.AppUser;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class AesServiceImpl implements AesService {
    @Autowired
    private ApplicationContext context;


    @Override
    public String encrypt(String plaintext, AppUser appUser)  {
        try {
            StringEncryptor oStringEncryptor = (StringEncryptor)context.getBean("jasyptStringEncryptor");

            String secret = oStringEncryptor.decrypt(appUser.getEcred()).split("/")[0];
            String iv = oStringEncryptor.decrypt(appUser.getEcred()).split("/")[1];
            String padding = appUser.getPadding();
            byte[] key = secret.getBytes("UTF-8");
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance(padding);

            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(iv));
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            //Perform Encryption
            return Base64.getEncoder().encodeToString(cipher.doFinal(plaintext.getBytes("UTF-8")));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | UnsupportedEncodingException |
                 IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(AesServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String decrypt(String encrypted, AppUser appUser) {
        try {
            // 1. Step: Decode base64 payload
            byte[] decodedBytes = Base64.getDecoder().decode(encrypted);
            System.out.println("📥 Encrypted payload (Base64): " + encrypted);
            System.out.println("📦 Decoded byte length: " + decodedBytes.length);

            // 2. Step: Get cipher transformation from appUser padding
            String transformation = appUser.getPadding(); // e.g., "AES/CBC/PKCS5Padding"
            System.out.println("🔐 Cipher Transformation: " + transformation);

            // 3. Step: Decrypt user's stored credentials (key and iv)
            StringEncryptor stringEncryptor = (StringEncryptor) context.getBean("jasyptStringEncryptor");
            String decryptedEcred = stringEncryptor.decrypt(appUser.getEcred());

            System.out.println("🔓 Decrypted ecred: " + decryptedEcred);

            String[] parts = decryptedEcred.split("/");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid ecred format. Expected: key/iv");
            }

            String keyHex = parts[0];
            String ivHex = parts[1];

            System.out.println("🔑 Decrypted Key (hex): " + keyHex);
            System.out.println("🧊 Decrypted IV (hex): " + ivHex);

            // 4. Step: Convert key and IV from hex to bytes
            byte[] keyBytes = keyHex.getBytes(StandardCharsets.UTF_8); // If you stored the raw key directly
            byte[] ivBytes = hexStringToByteArray(ivHex);

            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            // 5. Step: Decrypt the actual payload
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            String decrypted = new String(decryptedBytes, StandardCharsets.UTF_8);

            System.out.println("✅ Final Decrypted Payload: " + decrypted);
            return decrypted;

        } catch (IllegalArgumentException e) {
            System.err.println("❌ Invalid payload. Ensure the input is a properly encoded Base64 string.");
            throw new IllegalArgumentException("Invalid payload. Ensure the input is a properly encoded Base64 string.", e);

        } catch (Exception e) {
            System.err.println("❌ Decryption failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Decryption failed.", e);
        }
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
