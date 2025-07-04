package com.chh.trustfort.accounting.dto;

import lombok.*;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@Data
@Builder
public class BalanceSheetLineItemDTO {
    private String section;
    private String accountName;
    private BigDecimal amount;
}

