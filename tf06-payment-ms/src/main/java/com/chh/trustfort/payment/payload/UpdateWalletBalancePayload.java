package com.chh.trustfort.payment.payload;

import lombok.Data;

@Data
public class UpdateWalletBalancePayload {
    private String userId;
    private double amount;
}
