package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.enums.AccountType;
import lombok.Data;

@Data
public class CreateChartOfAccountRequest {
    private String accountCode;
    private String accountName;
    private AccountClassification classification; // e.g., REVENUE, ASSET
    private AccountType accountType;              // e.g., INCOME, EXPENSE
    private String department;
    private String businessUnit;
}
