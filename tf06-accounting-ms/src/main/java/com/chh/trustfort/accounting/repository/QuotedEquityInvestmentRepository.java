package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.QuotedEquityInvestment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuotedEquityInvestmentRepository extends JpaRepository<QuotedEquityInvestment, Long> {
}
