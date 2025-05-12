package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.enums.TaxType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaxFilingSummaryDTO {

    private TaxType taxType;
    private BigDecimal totalTaxAmount;
}
