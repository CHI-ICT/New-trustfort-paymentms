package com.chh.trustfort.payment.security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

import static com.chh.trustfort.payment.security.AesServiceImpl.hexStringToByteArray;

public class AESUtils {
      
//     public static String encrypt(String stringToEncrypt) {
//        try {
//             String secret = "99A47258y83921B1627495826M729361";
//            String iv = "1234567890123456";
//            String padding = "AES/CBC/PKCS5Padding";
//            byte[] key = secret.getBytes("UTF-8");
//            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
//            Cipher cipher = Cipher.getInstance(padding);
//
//            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
//            IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(iv));
//            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
//
//            //Perform Encryption
//            return Base64.getEncoder().encodeToString(cipher.doFinal(stringToEncrypt.getBytes("UTF-8")));
//        } catch (Exception  ex) {
//            System.err.println("Encryption error: " + ex.getMessage());
//        }
//        return null;
//    }
public static String encrypt(String stringToEncrypt) {
    try {
        String secret = "99A47258y83921B1627495826M729361";
        String iv = "1234567890123456";
        String padding = "AES/CBC/PKCS5Padding";

        // Derive 16-byte key using MD5 hash
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] keyBytes = md.digest(secret.getBytes("UTF-8"));
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance(padding);
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes("UTF-8"));

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(stringToEncrypt.getBytes("UTF-8"));
        return Base64.getUrlEncoder().encodeToString(encrypted); // Use URL-safe encoding
    } catch (Exception ex) {
        System.err.println("Encryption error: " + ex.getMessage());
    }
    return null;
}



//     public static void main(String[] args) {
//        
//        AESUtils oAESUtils = new AESUtils();
//
//        oAESUtils.e
//  
//           // BCrypt();
//
//        
//    }
    
}
