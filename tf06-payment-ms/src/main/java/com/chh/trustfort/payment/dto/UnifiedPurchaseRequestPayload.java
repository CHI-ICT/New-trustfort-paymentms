package com.chh.trustfort.payment.dto;

import com.chh.trustfort.payment.enums.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UnifiedPurchaseRequestPayload {
    private String userId;
    private BigDecimal amount;
    private String productName;
    private String narration;
    private String stringifiedData;
    private PaymentMethod paymentMethod; // WALLET, PAYSTACK, FLUTTERWAVE, OPEN_BANKING
}
