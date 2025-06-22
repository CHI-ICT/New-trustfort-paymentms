package com.chh.trustfort.payment.payload;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FundsTransferRequestPayload {
    private String senderWalletId;
    private String receiverWalletId;
    private BigDecimal amount;
    private String narration;
    private String transactionPin;
}
