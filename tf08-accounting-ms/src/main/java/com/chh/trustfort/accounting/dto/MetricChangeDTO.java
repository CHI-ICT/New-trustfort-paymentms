package com.chh.trustfort.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MetricChangeDTO {
    private String metric;
    private BigDecimal previousValue;
    private BigDecimal currentValue;
    private BigDecimal changeAmount;
    private BigDecimal changePercentage;
    private String trend; // STABLE, INCREASED, DROPPED
}
