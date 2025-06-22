package com.chh.trustfort.accounting.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProfitAndLossDTO {
    private String category; // e.g., "Revenue", "Expenses"
    private String accountName;
    private BigDecimal amount;
}
