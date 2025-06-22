package com.chh.trustfort.accounting.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author dofoleta
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

    private Long id;
    private String createdAt;
    private String accountNumber;
    private String category;
    private String productCode;
    private String branchCode;
    private String customerNumber;
    private String status;
    private BigDecimal availableBalance = BigDecimal.ZERO;
    private BigDecimal ledgerBalance = BigDecimal.ZERO;
    private String accountName;
    private String responseCode;
    private String currencyCode;
    private String restrictionCode;
    private String responseMessage;
    private String lastDebitDate;
    private String lastCreditDate;
}
