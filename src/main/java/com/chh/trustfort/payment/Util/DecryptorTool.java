package com.chh.trustfort.payment.Util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import java.util.Base64;

public class DecryptorTool {

    public static void main(String[] args) throws Exception {
        String encryptedIdToken = "v79z/XGZl4AkCrRYLBxeY7/LIJ734DpUK44a2UIN7GYzaHxsc+qFhrkGDmRVw4fhxtXMcEGmLEtaZl2cz4QMGyL016iSJJoesQ02j6KA95yy/Tzqr3sk39gyp8WQtTAK0AhBwePa+hyapEvrIwnjeWAqFC8sntbhKRPsTKHdlgwd81Kq0Fr2vlMu6vc1EWCrMjI4aEtKWPtaDcCCcdwFytoWpyxa3usUqZeruU+DIEVagV2tJOGoExsB45NM+bmDMO5r6q3Yl8Q0WWqidb0bd9YvoL32ldhYAJgA6ZaI7YluYptHV3EIv27NlPxtMfUb2K3R4iXsNdLAyK5+6FCQJ67hCvDHzmPQFlz7dtbl+lt6h4iBK6UXSo8+A6ff9qdeq0IwQdhdioDDvA7msflP5vIvGM3qlce5EQrHLF88Dp5BIHf1u71Cq5dRUz2v/j5Q";
        String ecred = "3yGm0Iq/BACzr/ueu7wHUgMEFDPW1KBKqM3OR0YJEqss/2HtjIKnRZ9B/FY5jbs9h/jTNlVbM5MBZuri8c41ZjroQ4F/QqVYkkyc01XCDCM=";
        String encryptionKey = "hNOJC7azs86txXCpBCLFmxqehFwKGWuE7h7lKuzvLw94QmnzMvLnEA8hPHSB7afD";

        // Step 1: Decrypt the ecred using Jasypt
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(encryptionKey); // this is your secret key to decrypt ecred
        String decryptedEcred = encryptor.decrypt(ecred);

        String[] parts = decryptedEcred.split("/");
        String aesKey = parts[0]; // hex string
        String ivHex = parts[1];  // hex string

        byte[] keyBytes = aesKey.getBytes("UTF-8");
        byte[] ivBytes = hexToBytes(ivHex);

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        // Step 2: AES Decrypt the encrypted token
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] decodedEncrypted = Base64.getDecoder().decode(encryptedIdToken);
        byte[] decryptedBytes = cipher.doFinal(decodedEncrypted);

        String decryptedText = new String(decryptedBytes, "UTF-8");
        System.out.println("✅ Decrypted ID Token: \n" + decryptedText);
    }

    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
