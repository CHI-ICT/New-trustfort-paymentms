package com.chh.trustfort.accounting.payload;

import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.enums.AccountStatus;
import com.chh.trustfort.accounting.enums.Subsidiary;
import com.chh.trustfort.accounting.enums.TransactionType;
import lombok.Data;

@Data
public class CreateCOARequestPayload {
    private String name;
    private Long categoryId;
    private Long parentAccountId;
    private Subsidiary subsidiary;
    private String currency;
    private TransactionType normalBalance;
    private AccountStatus status;
    private String departmentCode;
    private AccountClassification classification;
}

