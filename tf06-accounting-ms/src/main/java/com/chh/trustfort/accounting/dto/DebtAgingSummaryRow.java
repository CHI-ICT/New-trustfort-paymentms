package com.chh.trustfort.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DebtAgingSummaryRow {
    private String customerName;
    private BigDecimal current;
    private BigDecimal days30;
    private BigDecimal days60;
    private BigDecimal days90;
    private BigDecimal over90;
}
