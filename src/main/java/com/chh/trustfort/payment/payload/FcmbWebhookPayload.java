package com.chh.trustfort.payment.payload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FcmbWebhookPayload {
    private String reference; // should match referenceCode
    private BigDecimal amount;
}
