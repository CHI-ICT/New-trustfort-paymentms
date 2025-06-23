package com.chh.trustfort.payment.payload;

import lombok.Data;

@Data
public class UpdateWalletBalancePayload {
    private String walletId;
    private double amount;
}
