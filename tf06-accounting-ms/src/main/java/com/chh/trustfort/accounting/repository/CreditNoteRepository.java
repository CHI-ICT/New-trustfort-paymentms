package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.CreditNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditNoteRepository extends JpaRepository<CreditNote, Long> {
}
