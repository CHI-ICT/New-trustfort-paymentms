package com.chh.trustfort.accounting.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CashFlowStatementResponse {
    private BigDecimal cashFromOperatingActivities;
    private BigDecimal cashFromInvestingActivities;
    private BigDecimal cashFromFinancingActivities;
    private BigDecimal openingCashBalance;
    private BigDecimal closingCashBalance; // ✅ Needed for integrity check
}
