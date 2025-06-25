package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.enums.TaxType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReconciliationResultDTO {
    private String bankReference;
    private TaxType taxType;
    private BigDecimal expectedAmount;
    private BigDecimal amount;
    private LocalDate date;
    private BigDecimal postedAmount;
    private BigDecimal discrepancyAmount;
    private String matchStatus; // MATCHED / UNMATCHED
}
