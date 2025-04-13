
package com.chh.trustfort.payment.payload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentReferenceResponsePayload {
    private String referenceCode;
    private BigDecimal amount;
    private String status;
    private String message;
}

