package com.chh.trustfort.accounting.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author dofoleta
 */
@Getter
@Setter
public class LastTransactionDTO {

    private String beneficiaryBankLogo;
    private String beneficiaryName;
    private String beneficiaryAccount;
    private String beneficiaryBank;
    private String senderBankLogo;
    private String senderName;
    private String senderAccount;
    private String senderBank;
    private String date;
    private BigDecimal amount;
    private String status;
    private String referenceId;
    private String description;
    private String transferType;
    private BigDecimal fees;
    private String transactionType;
}
