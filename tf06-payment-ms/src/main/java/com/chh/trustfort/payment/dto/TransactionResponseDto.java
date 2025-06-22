package com.chh.trustfort.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransactionResponseDto {
    private Long transactionId;
    private String message;
    private BigDecimal newBalance;
    private String transactionType;
    private BigDecimal amount;
    private String description;
    private Date transactionDate;
}
