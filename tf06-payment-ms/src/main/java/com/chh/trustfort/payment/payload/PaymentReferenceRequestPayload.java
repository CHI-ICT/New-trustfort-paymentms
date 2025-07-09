package com.chh.trustfort.payment.payload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentReferenceRequestPayload {
    private String userId;
    private BigDecimal amount;
}
