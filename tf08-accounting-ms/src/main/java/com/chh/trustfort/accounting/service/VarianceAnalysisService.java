package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.VarianceAnalysisDTO;

import java.time.LocalDate;

public interface VarianceAnalysisService {
    VarianceAnalysisDTO analyzeVariance(LocalDate startDate, LocalDate endDate);
}
