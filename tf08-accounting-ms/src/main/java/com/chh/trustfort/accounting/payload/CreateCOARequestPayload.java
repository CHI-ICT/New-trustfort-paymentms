package com.chh.trustfort.accounting.payload;

import com.chh.trustfort.accounting.enums.*;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

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
    @Enumerated(EnumType.STRING)
    private ExpenseType expenseType;

}

