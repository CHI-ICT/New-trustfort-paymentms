package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;



@Repository("accountingUsersRepository")
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUserName(String userName);

    Users getUserByUserName(String sub);

    Optional<Users> findByEmail(String email);

    Optional<Users> findByPhoneNumber(String phoneNumber);


}
