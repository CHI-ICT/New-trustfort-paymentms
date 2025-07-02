package com.chh.trustfort.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VerifyFlutterwaveRequest {
    private String transactionId;
    private String txRef;
    private BigDecimal expectedAmount;
    private String expectedCurrency;
}
