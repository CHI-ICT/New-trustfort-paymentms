package com.chh.trustfort.payment.payload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditCommissionPayload {
    private BigDecimal amount;
    private String source;
    private String reference;
}
