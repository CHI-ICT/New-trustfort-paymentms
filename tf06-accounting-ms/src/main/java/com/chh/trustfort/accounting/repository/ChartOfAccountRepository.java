package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChartOfAccountRepository extends JpaRepository<ChartOfAccount, Long> {
    List<ChartOfAccount> findByClassification(AccountClassification classification);
    @Query("SELECT c FROM ChartOfAccount c WHERE c.accountCode = :accountCode")
    Optional<ChartOfAccount> findByAccountCode(@Param("accountCode") String accountCode);
}
