package com.chh.trustfort.payment.payload;

import com.chh.trustfort.payment.enums.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FundWalletRequestPayload {
    private String userId; // Always the RECEIVER (i.e. who is getting the funds)
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod; // WALLET, PAYSTACK, FLUTTERWAVE, BANK_TRANSFER
    private String narration;
}

