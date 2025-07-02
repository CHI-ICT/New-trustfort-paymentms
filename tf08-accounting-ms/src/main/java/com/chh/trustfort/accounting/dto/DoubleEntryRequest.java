package com.chh.trustfort.accounting.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class DoubleEntryRequest {
    private String debitAccountCode;
    private String creditAccountCode;
    private BigDecimal amount;
    private String reference;
    private String description;
    private String department;
    private String businessUnit;
    private LocalDateTime transactionDate;
}
