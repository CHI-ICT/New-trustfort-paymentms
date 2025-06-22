package com.chh.trustfort.payment.security;

import com.chh.trustfort.payment.model.AppUser;

/**
 *
 * @author dofoleta
 */
public interface AesService {

    public String decrypt(String cipherText, AppUser appUser);

    public String encrypt(String plaintext, AppUser appUser);
}

