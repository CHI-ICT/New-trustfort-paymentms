package com.chh.trustfort.accounting.security;

import com.chh.trustfort.accounting.model.AppUser;

/**
 *
 * @author dofoleta
 */
public interface AesService {

    public String decrypt(String cipherText, AppUser appUser);

    public String encrypt(String plaintext, AppUser appUser);
}
