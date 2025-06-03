package com.chh.trustfort.accounting.dto.investment;

import com.chh.trustfort.accounting.enums.InsuranceProductType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvestmentRuleRequestDTO {
    public InsuranceProductType insuranceType;
    public Long assetClassId;
    public Long minTenor;
    public BigDecimal maxAmount;
    public boolean allowHighRisk;
    public String createdBy;
}