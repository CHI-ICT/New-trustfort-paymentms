package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
}
