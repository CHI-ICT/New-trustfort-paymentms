package com.chh.trustfort.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ReportRow {
    private String description;
    private BigDecimal amount;
}
