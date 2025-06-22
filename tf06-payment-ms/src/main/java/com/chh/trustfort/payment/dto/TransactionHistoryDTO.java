package com.chh.trustfort.payment.dto;

import java.math.BigDecimal;

/**
 *
 * @author dofoleta
 */
@lombok.Data
public class TransactionHistoryDTO {

    private String postDate;
    private String referenceNumber;
    private String narration;
    private String description;
    private String valueDate;
    private String debitAmount;
    private String creditAmount;
    private BigDecimal balance;
    private BigDecimal amount;
    private String drcrFlag;

    private String recipientName;
    private String recipientBank;
    private String transactionType;
    private String transactionReceipt;

    private String originatorName;
    private String senderBank;
    private String sessionId;

    private String meterNumber;
    private String electricityUnit;
    private String electricityToken;
    
    private BigDecimal fee;
    private String bankLogo;
    
    private String senderAccount;
    private String beneficiaryAccount;

}
