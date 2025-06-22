package com.chh.trustfort.accounting.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EquityStatementDTO {
    private BigDecimal openingEquity;
    private BigDecimal retainedEarnings;
    private BigDecimal netIncome;
    private BigDecimal dividends;
    private BigDecimal capitalContribution;
    private BigDecimal closingEquity;
}
