package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.EvidenceOfPayment;
import com.chh.trustfort.accounting.model.PayableInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// EOPRepository.java
public interface EOPRepository extends JpaRepository<EvidenceOfPayment, Long> {
    Optional<EvidenceOfPayment> findByInvoice(PayableInvoice invoice);
}