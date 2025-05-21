package com.chh.trustfort.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MultiCurrencyReportRow {
    private String receiptNumber;
    private String payerName;
    private String currency;
    private BigDecimal originalAmount;
    private BigDecimal exchangeRate;
    private BigDecimal baseAmount;
    private LocalDateTime receiptDate;
}
