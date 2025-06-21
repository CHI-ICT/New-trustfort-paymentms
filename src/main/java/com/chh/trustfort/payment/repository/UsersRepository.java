package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.model.Users;
import lombok.Getter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUserName(String userName);

    Users getUserByUserName(String sub);

    Optional<Users> findByEmailAddress(String emailAddress);

}
