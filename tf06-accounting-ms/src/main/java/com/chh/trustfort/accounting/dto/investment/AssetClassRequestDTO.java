package com.chh.trustfort.accounting.dto.investment;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AssetClassRequestDTO {
    private String name;
    private BigDecimal averageReturnRate;
    private String riskLevel;
    private String regulatorCode;
}