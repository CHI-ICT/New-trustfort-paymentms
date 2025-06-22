package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.enums.AccountClassification;
import com.chh.trustfort.accounting.enums.TransactionType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StatementFilterDTO {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    private String businessUnit;
    private String department;
    private AccountClassification classification; // e.g., REVENUE, EXPENSE
    private TransactionType transactionType;      // e.g., DEBIT, CREDIT
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
}
