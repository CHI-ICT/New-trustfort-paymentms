package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.ChartOfAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChartOfAccountAccountRepository extends JpaRepository<ChartOfAccount, Long> {
    boolean existsByCode(String code);
}
