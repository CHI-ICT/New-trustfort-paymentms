package com.chh.trustfort.gateway.security;

import com.chh.trustfort.gateway.model.AppUser;

/**
 *
 * @author dofoleta
 */
public interface AesService {

    public String decrypt(String cipherText, AppUser appUser);

    public String encrypt(String plaintext, AppUser appUser);
}
