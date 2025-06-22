package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.PaymentDiscrepancy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentDiscrepancyRepository extends JpaRepository<PaymentDiscrepancy, Long> {
}
