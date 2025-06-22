package com.chh.trustfort.accounting.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ConsolidatedSummaryDTO {
    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal totalAssets;
    private BigDecimal totalLiabilities;
    private BigDecimal totalEquity;
    private BigDecimal totalRevenue;
    private BigDecimal totalExpenses;

    private BigDecimal netIncome;
}
