package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.MoneyMarketInvestment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyMarketInvestmentRepository  extends JpaRepository<MoneyMarketInvestment, Long> {
}
