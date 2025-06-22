package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    List<BankTransaction> findAllByMatchedFalse();
}
