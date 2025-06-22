package com.chh.trustfort.accounting.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CashFlowDTO {
    private String activityType; // Operating, Investing, Financing
    private String description;
    private BigDecimal amount;
}
