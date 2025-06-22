package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.FinancialSummaryDTO;

import java.time.LocalDate;

public interface FinancialSummaryService {
    FinancialSummaryDTO compileSummary(LocalDate startDate, LocalDate endDate);
}
