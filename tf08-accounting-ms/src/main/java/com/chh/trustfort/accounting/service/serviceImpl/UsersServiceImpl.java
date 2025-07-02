package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.model.Users;
import com.chh.trustfort.accounting.repository.UsersRepository;
import com.chh.trustfort.accounting.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl implements UsersService {

    private static final Logger log = LoggerFactory.getLogger(UsersServiceImpl.class);

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public Users getUserById(Long id) {
        return usersRepository.findById(id).orElse(null);
    }

    @Override
    public Users getUserByUserName(String userName) {
        Users user = usersRepository.findByUserName(userName).orElse(null);
        if (user == null) {
            log.error("User not found in database: {}", userName);
        } else {
            log.info("User found: {}", user.getUserName());
        }
        return user;
    }

    @Override
    public Users saveUser(Users user) {
        return usersRepository.save(user);
    }
}
