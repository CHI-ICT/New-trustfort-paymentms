package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.EurobondInvestment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EurobondInvestmentRepository extends JpaRepository<EurobondInvestment, Long> {
}
