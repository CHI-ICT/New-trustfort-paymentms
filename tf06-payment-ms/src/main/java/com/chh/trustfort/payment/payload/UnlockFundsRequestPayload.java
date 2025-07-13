package com.chh.trustfort.payment.payload;

import lombok.Data;

/**
 *
 * @author DOfoleta
 */
@Data
public class UnlockFundsRequestPayload {
    private String userId;
    private String reason;
}
