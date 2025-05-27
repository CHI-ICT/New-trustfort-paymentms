package com.chh.trustfort.accounting.dto.investment;

import com.chh.trustfort.accounting.enums.InsuranceProductType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InvestmentRuleResponseDTO {
    public Long id;
    public InsuranceProductType insuranceType;
    public String assetClassName;
    public Integer minTenorYears;
    public BigDecimal maxAmount;
    public boolean allowHighRisk;
    public String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime createdAt;
}

