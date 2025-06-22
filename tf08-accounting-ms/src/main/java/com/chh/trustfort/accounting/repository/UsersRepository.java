package com.chh.trustfort.accounting.repository;


import com.chh.trustfort.accounting.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users>  findByUserName(String userName);

    Users getUserByUserName(String sub);

    Optional<Users> findByEmailAddress(String emailAddress);

}
