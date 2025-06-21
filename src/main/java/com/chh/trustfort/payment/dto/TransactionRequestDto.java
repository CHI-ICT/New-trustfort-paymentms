package com.chh.trustfort.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequestDto {
    private String walletId;
    private BigDecimal amount;
    private String transactionType; // "CREDIT" or "DEBIT"
    private String description; // Optional
}
