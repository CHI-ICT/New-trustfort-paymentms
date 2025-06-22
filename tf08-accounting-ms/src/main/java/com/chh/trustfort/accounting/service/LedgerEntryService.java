package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.LedgerEntry;
import com.chh.trustfort.accounting.repository.LedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LedgerEntryService {

    private final LedgerEntryRepository repository;

    public void postToGL(LedgerEntry entry) {
        repository.save(entry);
    }
}
