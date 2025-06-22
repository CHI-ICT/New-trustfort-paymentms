package com.chh.trustfort.payment.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class WithdrawCommissionRequest {
    private String walletId;
    private BigDecimal amount;
    private String otpCode;
    private String transactionPin;
}