package com.chh.trustfort.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashFlowStatementDTO {
    private BigDecimal operatingActivities = BigDecimal.ZERO;
    private BigDecimal investingActivities = BigDecimal.ZERO;
    private BigDecimal financingActivities = BigDecimal.ZERO;
    private BigDecimal netCashFlow = BigDecimal.ZERO;

    private BigDecimal openingCashBalance = BigDecimal.ZERO;    // ✅ Add this
    private BigDecimal closingCashBalance = BigDecimal.ZERO;
}