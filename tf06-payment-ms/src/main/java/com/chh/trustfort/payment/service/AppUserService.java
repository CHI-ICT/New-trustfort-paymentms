package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.model.AppUser;

public interface AppUserService {
    AppUser createUser(AppUser user);
    AppUser getUserById(Long id);
    AppUser getUserByUsername(String username);
}
