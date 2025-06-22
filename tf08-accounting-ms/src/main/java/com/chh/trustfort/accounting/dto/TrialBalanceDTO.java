package com.chh.trustfort.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrialBalanceDTO {
    private String accountCode;
    private String accountName;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private BigDecimal balance; // debit - credit
}
