package com.chh.trustfort.payment.payload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FundWalletRequestPayload {
    private BigDecimal amount;
    private String currency;
    private String walletId;



}
