package com.chh.trustfort.accounting.payload;

import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author DOfoleta
 */
@Data
public class FundWalletRequestPayload {
    BigDecimal amount;
    String walletId;
    
}
