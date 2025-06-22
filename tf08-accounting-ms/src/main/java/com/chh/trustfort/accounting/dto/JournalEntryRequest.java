package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.enums.TransactionType;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class JournalEntryRequest {
    private String accountCode;
    private BigDecimal amount;
    private TransactionType transactionType;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // <-- Key fix here
    private LocalDateTime transactionDate;

    private String description;
    private String reference;
    private String department;
    private String businessUnit;
    private ChartOfAccount chartOfAccount;
}
