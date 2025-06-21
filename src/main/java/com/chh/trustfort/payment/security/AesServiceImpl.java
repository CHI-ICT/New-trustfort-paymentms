package com.chh.trustfort.payment.security;

import com.chh.trustfort.payment.model.AppUser;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.jasypt.encryption.StringEncryptor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

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
            java.util.logging.Logger.getLogger(AesServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String decrypt(String cipherText, AppUser appUser) {
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
