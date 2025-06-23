package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.model.Users;

public interface UsersService {
    Users getUserById(Long id);
    Users getUserByUserName(String userName);
    Users saveUser(Users user);
}
