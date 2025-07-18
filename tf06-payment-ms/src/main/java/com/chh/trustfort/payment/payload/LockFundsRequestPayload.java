package com.chh.trustfort.payment.payload;

import lombok.Data;

import java.math.BigDecimal;

/**
 *
 * @author DOfoleta
 */

@Data
public class LockFundsRequestPayload {
    private String userId;
    private BigDecimal amount;
    private String reason;
}