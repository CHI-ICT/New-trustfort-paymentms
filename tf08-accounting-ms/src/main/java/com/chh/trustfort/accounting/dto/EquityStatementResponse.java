package com.chh.trustfort.accounting.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EquityStatementResponse {
    private BigDecimal openingEquity;
    private BigDecimal contributions;
    private BigDecimal retainedEarnings;
    private BigDecimal dividends;
    private BigDecimal closingEquity;
}
