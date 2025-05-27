package com.chh.trustfort.accounting.payload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TrialBalanceDTO {
    private String accountCode;
    private String accountName;
    private BigDecimal debit;
    private BigDecimal credit;
}
