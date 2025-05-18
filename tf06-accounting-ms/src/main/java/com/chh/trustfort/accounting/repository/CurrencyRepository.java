package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findByCode(String code);
    boolean existsByCode(String code);
}