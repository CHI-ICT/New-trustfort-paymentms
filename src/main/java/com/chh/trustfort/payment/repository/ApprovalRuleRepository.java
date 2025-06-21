package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.model.facility.ApprovalRule;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ApprovalRuleRepository extends JpaRepository<ApprovalRule, Long> {

    @Query("SELECT r FROM ApprovalRule r WHERE r.isDeleted = false ORDER BY r.minAmount, r.level")
    List<ApprovalRule> findAllActive();

    @Query("SELECT r FROM ApprovalRule r WHERE :amount BETWEEN r.minAmount AND r.maxAmount AND r.isDeleted = false ORDER BY r.level ASC")
    List<ApprovalRule> findByAmountRange(@Param("amount") BigDecimal amount);
    List<ApprovalRule> findAllByIsDeletedFalse();
    boolean existsByName(String name);
}


