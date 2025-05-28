package com.chh.trustfort.accounting.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvestmentCalculationResultDTO {
    private BigDecimal roi;
    private BigDecimal interest;
    private BigDecimal dividends;
}
