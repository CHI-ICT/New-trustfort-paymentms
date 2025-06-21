package com.chh.trustfort.payment.payload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConfirmBankTransferRequest {
    private String accountNumber;
    private BigDecimal amount;
    private String reference;
}
