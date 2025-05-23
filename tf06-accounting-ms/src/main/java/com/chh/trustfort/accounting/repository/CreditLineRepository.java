package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.enums.CreditStatus;
import com.chh.trustfort.accounting.model.CreditLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreditLineRepository extends JpaRepository<CreditLine, Long> {

    List<CreditLine> findByUserId(Long userId);

    List<CreditLine> findByStatus(CreditStatus status);

    List<CreditLine> findByUserIdAndStatus(Long userId, CreditStatus status);

    Optional<CreditLine> findTopByUserIdOrderByRequestedAtDesc(Long userId);
}

