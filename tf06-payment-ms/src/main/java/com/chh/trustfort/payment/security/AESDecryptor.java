package com.chh.trustfort.payment.security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;

public class AESDecryptor {

    private static final String SECRET_KEY = "99A47258y83921B1627495826M729361";
    private static final String IV = "1234567890123456"; // same as your encryption IV

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose an option:\n1. Encrypt\n2. Decrypt");
        String option = scanner.nextLine();

        try {
            if ("1".equals(option)) {
                System.out.println("Enter plaintext to encrypt:");
                String plainText = scanner.nextLine();
                String encrypted = encrypt(plainText, SECRET_KEY, IV);
                System.out.println("\nEncrypted (Base64 URL-safe):");
                System.out.println(encrypted);
            } else if ("2".equals(option)) {
                System.out.println("Enter encrypted Base64 text:");
                String encryptedText = scanner.nextLine();
                String decrypted = decrypt(encryptedText, SECRET_KEY, IV);
                System.out.println("\nDecrypted response:");
                System.out.println(decrypted);
            } else {
                System.out.println("Invalid option selected.");
            }
        } catch (Exception e) {
            System.err.println("Operation failed: " + e.getMessage());
        }
    }

    public static String encrypt(String plainText, String key, String iv) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] keyBytes = md.digest(key.getBytes("UTF-8"));
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes("UTF-8"));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));

        // Use standard Base64 (not URL-safe) to avoid `-`, `_`, etc.
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }


    public static String decrypt(String encrypted, String key, String iv) throws Exception {
        // Derive a 16-byte AES key using MD5 (same as your service)
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] keyBytes = md.digest(key.getBytes("UTF-8"));
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes("UTF-8"));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

        // Decode using URL-safe Base64 decoder
//        byte[] decoded = Base64.getUrlDecoder().decode(encrypted);
        byte[] decoded = Base64.getDecoder().decode(encrypted);
        byte[] decryptedBytes = cipher.doFinal(decoded);

        return new String(decryptedBytes, "UTF-8");
    }

}
