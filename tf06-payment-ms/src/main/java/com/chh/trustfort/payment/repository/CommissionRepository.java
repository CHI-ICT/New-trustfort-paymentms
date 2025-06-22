package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.enums.TransactionStatus;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.Commission;
import com.chh.trustfort.payment.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {
    List<Commission> findByUser(Users user);
    List<Commission> findByUserAndStatus(AppUser user, TransactionStatus status);
}
