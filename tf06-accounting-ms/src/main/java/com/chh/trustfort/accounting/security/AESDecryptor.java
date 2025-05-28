package com.chh.trustfort.accounting.security;

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
        System.out.println("Enter encrypted Base64 text:");
        String encryptedText = scanner.nextLine();

        try {
            String decrypted = decrypt(encryptedText, SECRET_KEY, IV);
            System.out.println("\nDecrypted response:");
            System.out.println(decrypted);
        } catch (Exception e) {
            System.err.println("Failed to decrypt: " + e.getMessage());
        }
    }

    public static String decrypt(String encrypted, String key, String iv) throws Exception {
        // Derive 16-byte AES key using MD5 (same as your service)
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] keyBytes = md.digest(key.getBytes("UTF-8"));

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes("UTF-8"));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

        byte[] decoded = Base64.getDecoder().decode(encrypted);
        byte[] decryptedBytes = cipher.doFinal(decoded);

        return new String(decryptedBytes, "UTF-8");
    }
}
