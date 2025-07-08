package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.AccountingLedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository("accountingLedgerEntryRepository")
public interface AccountingLedgerEntryRepository extends JpaRepository<AccountingLedgerEntry, Long> {
    List<AccountingLedgerEntry> findByTransactionDateBetween(LocalDate start, LocalDate end);
}
