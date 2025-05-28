package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.Investment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    List<Investment> findByMaturityDateBetweenAndMaturityNotifiedFalse(LocalDate start, LocalDate end);
    List<Investment> findByMaturityDateBeforeAndRolledOverFalse(LocalDate date);
}
