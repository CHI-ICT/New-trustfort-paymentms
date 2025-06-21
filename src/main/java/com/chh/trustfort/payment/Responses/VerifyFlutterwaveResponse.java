package com.chh.trustfort.payment.Responses;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VerifyFlutterwaveResponse {
    private String status;
    private String message;
    private BigDecimal creditedAmount;
    private String walletId;
}
