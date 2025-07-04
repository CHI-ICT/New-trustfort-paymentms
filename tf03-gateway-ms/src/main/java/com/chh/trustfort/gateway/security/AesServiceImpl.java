package com.chh.trustfort.gateway.security;

import com.chh.trustfort.gateway.model.AppUser;
import java.io.UnsupportedEncodingException;
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
import org.springframework.stereotype.Service;

/**
 *
 * @author dofoleta
 */
@Service
public class AesServiceImpl implements AesService {

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
