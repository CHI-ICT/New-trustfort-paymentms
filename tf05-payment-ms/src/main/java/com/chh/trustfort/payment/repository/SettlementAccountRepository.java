package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.model.SettlementAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementAccountRepository extends JpaRepository<SettlementAccount, Long> {
    SettlementAccount findByAccountNumber(String accountNumber);
}
