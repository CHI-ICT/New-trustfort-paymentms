package com.chh.trustfort.payment.payload;

import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author DOfoleta
 */
@Data
public class FundWalletRequestPayload {
    private String walletId;
    private BigDecimal amount;
    private String narration;
}
