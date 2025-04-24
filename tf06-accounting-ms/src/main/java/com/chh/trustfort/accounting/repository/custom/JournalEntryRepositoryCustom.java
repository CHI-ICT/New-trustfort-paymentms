package com.chh.trustfort.accounting.repository.custom;

import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.model.JournalEntry;

import java.util.List;

public interface JournalEntryRepositoryCustom {
    List<JournalEntry> findByStatementFilters(StatementFilterDTO filter);
}
