package com.chh.trustfort.payment.dto;

import java.math.BigDecimal;

public class WalletTransactionRequest {
    private String walletId;
    private BigDecimal amount;
    private String currency; // must be required
    private String narration;
}
