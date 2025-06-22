package com.chh.trustfort.payment.payload;

import lombok.Data;

import java.math.BigDecimal;

/**
 *
 * @author DOfoleta
 */
@Data
public class WithdrawFundsRequestPayload {
    private String walletId;
    private BigDecimal amount;
    private Long otpCode;
    private String transactionPin;
    private String accountNumber;
    private String bankCode;
    private String accountName;

}
