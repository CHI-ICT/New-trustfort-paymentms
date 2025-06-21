package com.chh.trustfort.payment.Responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class WalletBalanceResponse {
    private String responseCode;
    private String message;
    private String walletId;
    private BigDecimal balance;

    public WalletBalanceResponse() {} // Default constructor
}
