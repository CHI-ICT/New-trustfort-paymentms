// PaymentMovementRequest.java
package com.chh.trustfort.accounting.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentMovementRequest {
    private String sourceReceivableRef;
    private String destinationReceivableRef;
    private BigDecimal amount;
    private String movedBy;
    private String reason;
}