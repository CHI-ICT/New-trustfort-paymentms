package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.enums.TaxType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReconciliationResultDTO {
    private TaxType taxType;
    private BigDecimal expectedAmount;
    private BigDecimal postedAmount;
    private BigDecimal discrepancyAmount;
}
