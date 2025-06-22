package com.chh.trustfort.accounting.payload;

import com.chh.trustfort.accounting.model.Users;
import lombok.Data;

/**
 *
 * @author DOfoleta
 */

@Data
public class CreateWalletRequestPayload {
    private String walletId;
    private Users owner;
    private String currency;
}
