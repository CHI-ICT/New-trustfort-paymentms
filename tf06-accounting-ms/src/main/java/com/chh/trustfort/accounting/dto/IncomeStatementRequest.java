package com.chh.trustfort.accounting.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class IncomeStatementRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}
