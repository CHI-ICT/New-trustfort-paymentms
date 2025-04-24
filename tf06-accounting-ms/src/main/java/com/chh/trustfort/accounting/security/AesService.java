package com.chh.trustfort.accounting.security;

import com.chh.trustfort.accounting.model.AppUser;

/**
 *
 * @author dofoleta
 */
public interface AesService {
    //    String encrypt(String username, String data);
//    String decrypt(String username, String encryptedData);
    String encrypt(String plaintext, String keyWithIv);
    String decrypt(String cipherText, String keyWithIv);
}
