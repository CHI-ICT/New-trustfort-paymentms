package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.dto.FinancialSummaryDTO;
import com.chh.trustfort.accounting.dto.VarianceAnalysisDTO;
import com.chh.trustfort.accounting.dto.VarianceResultDTO;
import com.chh.trustfort.accounting.service.FinancialSummaryService;
import com.chh.trustfort.accounting.service.VarianceAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VarianceAnalysisServiceImpl implements VarianceAnalysisService {

    private final FinancialSummaryService financialSummaryService;

    @Override
    public VarianceAnalysisDTO analyzeVariance(LocalDate startDate, LocalDate endDate) {
        // Mocked projections for demonstration
        BigDecimal projectedRevenue = new BigDecimal("300000.00");
        BigDecimal projectedExpenses = new BigDecimal("200000.00");

        FinancialSummaryDTO summary = financialSummaryService.compileSummary(startDate, endDate);

        BigDecimal actualRevenue = summary.getIncomeStatement().getRevenue();
        BigDecimal actualExpenses = summary.getIncomeStatement().getExpenses();

        List<VarianceResultDTO> results = new ArrayList<>();
        results.add(buildResult("Revenue", actualRevenue, projectedRevenue));
        results.add(buildResult("Expenses", actualExpenses, projectedExpenses));

        return VarianceAnalysisDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .results(results)
                .currency(summary.getMetadata().getCurrency())
                .generatedBy(summary.getMetadata().getGeneratedBy())
                .generatedAt(summary.getMetadata().getGeneratedAt())
                .build();
    }

    private VarianceResultDTO buildResult(String metric, BigDecimal actual, BigDecimal projected) {
        BigDecimal varianceAmount = actual.subtract(projected);
        BigDecimal variancePercentage = projected.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : varianceAmount.divide(projected, 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));

        String status = "ON_TARGET";
        if (varianceAmount.compareTo(BigDecimal.ZERO) > 0) status = "OVER";
        else if (varianceAmount.compareTo(BigDecimal.ZERO) < 0) status = "UNDER";

        return VarianceResultDTO.builder()
                .metric(metric)
                .actual(actual)
                .projected(projected)
                .varianceAmount(varianceAmount)
                .variancePercentage(variancePercentage)
                .status(status)
                .build();
    }
}
