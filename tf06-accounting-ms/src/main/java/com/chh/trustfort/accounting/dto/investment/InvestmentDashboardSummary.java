package com.chh.trustfort.accounting.dto.investment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class InvestmentDashboardSummary {
    private BigDecimal totalInvested;
    private BigDecimal totalExpectedReturn;
    private Map<String, BigDecimal> byProductType;
    private Map<String, BigDecimal> byAssetClass;
}
