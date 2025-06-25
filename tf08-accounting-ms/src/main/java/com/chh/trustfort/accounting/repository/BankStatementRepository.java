package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.BankStatementRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BankStatementRepository extends JpaRepository<BankStatementRecord, Long> {
    List<BankStatementRecord> findByTransactionDateBetween(LocalDate start, LocalDate end);
}