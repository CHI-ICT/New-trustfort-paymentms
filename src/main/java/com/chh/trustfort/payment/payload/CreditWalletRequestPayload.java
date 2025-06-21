package com.chh.trustfort.payment.payload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditWalletRequestPayload {
    private BigDecimal amount;
    private String reference;
    private String email;

}
