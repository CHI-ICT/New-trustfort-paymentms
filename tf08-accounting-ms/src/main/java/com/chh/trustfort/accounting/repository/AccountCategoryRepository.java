package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.AccountCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountCategoryRepository extends JpaRepository<AccountCategory, Long> {
    Optional<AccountCategory> findByName(String name);
}

