package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository("accountingLedgerEntryRepository")
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    List<LedgerEntry> findByTransactionDateBetween(LocalDate start, LocalDate end);
}
