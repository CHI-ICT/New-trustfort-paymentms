package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.Subsidiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubsidiaryRepository extends JpaRepository<Subsidiary, Long> {
    Optional<Subsidiary> findByCode(String code);
    boolean existsByCode(String code);
}