package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.enums.InsuranceProductType;
import com.chh.trustfort.accounting.model.InvestmentRule;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvestmentRuleRepository extends JpaRepository<InvestmentRule, Long> {
    @Query("SELECT r FROM InvestmentRule r WHERE r.insuranceType = :type AND (r.assetClass.id = :assetClassId OR r.assetClass IS NULL)")
    List<InvestmentRule> findApplicable(@Param("type") InsuranceProductType type, @Param("assetClassId") Long assetClassId);
}

