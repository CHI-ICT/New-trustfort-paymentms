package com.chh.trustfort.payment.payload;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FundsTransferRequestPayload {
    private String senderUserId;
    private String receiverUserId;
    private BigDecimal amount;
    private String narration;
//    private String transactionPin;
}
