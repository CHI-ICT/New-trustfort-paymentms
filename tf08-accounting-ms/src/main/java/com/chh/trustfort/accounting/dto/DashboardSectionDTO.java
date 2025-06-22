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
public class DashboardSectionDTO {
    private String title;
    private BigDecimal value;
    private String currency;
    private String status; // e.g., POSITIVE / NEGATIVE / NEUTRAL
}
