package com.chh.trustfort.accounting.payload;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class CreditLineRequestDto {
    private Long userId;
    private BigDecimal amount;
    private String reason;
}
