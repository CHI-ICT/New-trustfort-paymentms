package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.Users;


public interface UsersService {
    Users getUserById(Long id);
    Users getUserByUserName(String userName);
    Users saveUser(Users user);
}
