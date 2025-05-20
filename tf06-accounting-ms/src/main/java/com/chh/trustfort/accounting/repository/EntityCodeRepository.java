package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.enums.Subsidiary;
import com.chh.trustfort.accounting.model.EntityCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EntityCodeRepository extends JpaRepository<EntityCode, Long> {
    Optional<EntityCode> findBySubsidiary(Subsidiary subsidiary);
}

