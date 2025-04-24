package com.chh.trustfort.accounting.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BalanceSheetFilterRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String department;
    private String businessUnit;
}
