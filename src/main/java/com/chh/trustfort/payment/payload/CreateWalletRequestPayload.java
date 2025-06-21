package com.chh.trustfort.payment.payload;

import com.chh.trustfort.payment.model.Users;
import lombok.Data;

/**
 *
 * @author DOfoleta
 */

@Data
public class CreateWalletRequestPayload {
    private String currency;
}
