package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.DebitNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DebitNoteRepository extends JpaRepository<DebitNote, Long> {
}
