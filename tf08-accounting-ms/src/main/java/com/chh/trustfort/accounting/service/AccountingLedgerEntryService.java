package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.AccountingLedgerEntry;
import com.chh.trustfort.accounting.repository.AccountingLedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountingLedgerEntryService {

    private final AccountingLedgerEntryRepository repository;

    public void postToGL(AccountingLedgerEntry entry) {
        repository.save(entry);
    }
}
