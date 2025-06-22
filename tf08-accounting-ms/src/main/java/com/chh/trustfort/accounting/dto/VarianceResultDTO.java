package com.chh.trustfort.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// VarianceResultDTO.java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VarianceResultDTO {
    private String metric;
    private BigDecimal actual;
    private BigDecimal projected;
    private BigDecimal varianceAmount;
    private BigDecimal variancePercentage;
    private String status; // e.g., "ON_TARGET", "OVER", "UNDER"
}
