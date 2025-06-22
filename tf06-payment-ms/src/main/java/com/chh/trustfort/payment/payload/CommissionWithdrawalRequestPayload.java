package com.chh.trustfort.payment.payload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CommissionWithdrawalRequestPayload {
    private BigDecimal amount;
    private String transactionPin;
    private String otpCode;
    private String bankCode;
    private String accountNumber;
    private String accountName;
}
