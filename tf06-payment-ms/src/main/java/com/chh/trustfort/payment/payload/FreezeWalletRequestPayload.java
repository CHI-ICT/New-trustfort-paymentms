package com.chh.trustfort.payment.payload;

import lombok.Data;

/**
 *
 * @author DOfoleta
 */
@Data
public class FreezeWalletRequestPayload {
    private String walletId;
    private String reason;
}
