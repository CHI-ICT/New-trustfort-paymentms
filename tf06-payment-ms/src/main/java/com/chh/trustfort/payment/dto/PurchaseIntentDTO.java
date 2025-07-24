package com.chh.trustfort.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseIntentDTO {
    private String userId;              // Phone number of the buyer
    private BigDecimal amount;         // Amount to be paid
    private String stringifiedData;    // JSON or plain string about the product/policy being paid for
}
