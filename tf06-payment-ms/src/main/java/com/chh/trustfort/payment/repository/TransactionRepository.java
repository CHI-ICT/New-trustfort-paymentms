package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Retrieve transactions for a wallet within a date range
    List<Transaction> findByWalletIdAndTransactionDateBetween(String walletId, Date startDate, Date endDate);
}
