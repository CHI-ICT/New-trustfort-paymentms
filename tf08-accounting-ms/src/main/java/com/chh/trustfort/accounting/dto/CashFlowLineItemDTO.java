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
public class CashFlowLineItemDTO {
    private String section;      // e.g., "Operating Activities"
    private String accountName;  // e.g., "Cash Sales"
    private BigDecimal amount;
}
