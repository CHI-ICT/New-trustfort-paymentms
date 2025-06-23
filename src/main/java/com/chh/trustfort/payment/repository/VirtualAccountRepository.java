package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.model.VirtualAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VirtualAccountRepository extends JpaRepository<VirtualAccount, Long> {
    boolean existsByAccountNumber(String accountNumber);
    Optional<VirtualAccount> findByAccountNumber(String accountNumber);

}
