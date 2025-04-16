package com.chh.trustfort.accounting.payload;

import lombok.Data;

/**
 *
 * @author DOfoleta
 */

@Data
public class CreateWalletResponsePayload {
    private String walletId;
    private String responseCode;
    private String responseMessage;
}
