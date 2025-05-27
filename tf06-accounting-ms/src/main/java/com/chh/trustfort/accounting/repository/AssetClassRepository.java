package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.AssetClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetClassRepository extends JpaRepository<AssetClass, Long> {
    boolean existsByName(String name);
}

