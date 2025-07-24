package com.chh.trustfort.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductPurchaseDTO {
    private String userId;
    private BigDecimal amount;
    private String productName;
    private String narration;
    private String stringifiedData;
}
