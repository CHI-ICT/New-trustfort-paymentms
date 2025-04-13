package com.chh.trustfort.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConfirmBankTransferRequest {
    private String accountNumber;
    private BigDecimal amount;

    public String getReference() {
        return getReference();
    }
}
