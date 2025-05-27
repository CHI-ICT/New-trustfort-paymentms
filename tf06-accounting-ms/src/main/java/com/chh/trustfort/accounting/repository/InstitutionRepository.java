package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    boolean existsByName(String name);
}
