package com.chh.trustfort.accounting.dto.investment;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentCalculationResultDTO {
    private BigDecimal interest;
    private BigDecimal expectedReturn;
    private BigDecimal dividends;
    private BigDecimal roi;
}