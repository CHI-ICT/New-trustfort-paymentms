package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.repository.AppUserRepository;
import com.chh.trustfort.payment.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AppUserServiceImpl implements AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AppUser createUser(AppUser user) {
        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return appUserRepository.createAppUser(user);
    }

    @Override
    public AppUser getUserById(Long id) {
        return appUserRepository.getAppUserById(id);
    }

    @Override
    public AppUser getUserByUsername(String username) {
        return appUserRepository.getAppUserByUserName(username);
    }
}
