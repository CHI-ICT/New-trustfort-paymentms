package com.chh.trustfort.payment.Responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class WalletBalanceResponse {
    private String responseCode;
    private String message;
    private String userId;
    private BigDecimal balance;
    private BigDecimal ledgerBalance;

    public WalletBalanceResponse() {} // Default constructor
}
