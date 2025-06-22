package com.chh.trustfort.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrialBalanceResponse {

    private String accountCode;
    private String accountName;
    private String description;
    private String classification; // ASSET, LIABILITY, etc.
    private String departmentCode;
    private String currency;
    private String subsidiary;
    private String fullAccountCode;
    private String currencyPrefixedCode;
    private String normalBalance;
    private String expenseType;
    private String accountStatus; // ACTIVE, INACTIVE
    private String expectedNature; // DEBIT or CREDIT
    private Boolean hasActivity; // true if any transaction was recorded
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private BigDecimal balance;
    private String formattedBalance; // e.g. "NGN 24,000.00 (CR)"
}
