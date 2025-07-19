package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.model.ChartOfAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportLineItem {
    private String label;
    private BigDecimal amount;
    private ChartOfAccount account; // assuming ChartOfAccount contains code/name/classification

}
