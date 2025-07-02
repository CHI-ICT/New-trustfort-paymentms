package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.dto.FinancialSummaryDTO;
import com.chh.trustfort.accounting.dto.MetricChangeDTO;
import com.chh.trustfort.accounting.dto.RealTimeMonitoringDTO;
import com.chh.trustfort.accounting.service.FinancialSummaryService;
import com.chh.trustfort.accounting.service.RealTimeMonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RealTimeMonitoringServiceImpl implements RealTimeMonitoringService {

    private final FinancialSummaryService financialSummaryService;

    @Override
    public RealTimeMonitoringDTO monitorTrends(LocalDate prevStart, LocalDate prevEnd, LocalDate currStart, LocalDate currEnd) {
        FinancialSummaryDTO prev = financialSummaryService.compileSummary(prevStart, prevEnd);
        FinancialSummaryDTO curr = financialSummaryService.compileSummary(currStart, currEnd);

        List<MetricChangeDTO> changes = new ArrayList<>();
        changes.add(compareMetric("Revenue", prev.getIncomeStatement().getRevenue(), curr.getIncomeStatement().getRevenue()));
        changes.add(compareMetric("Expenses", prev.getIncomeStatement().getExpenses(), curr.getIncomeStatement().getExpenses()));
        changes.add(compareMetric("Net Profit", prev.getIncomeStatement().getNetProfit(), curr.getIncomeStatement().getNetProfit()));
        changes.add(compareMetric("Assets", prev.getBalanceSheet().getAssets(), curr.getBalanceSheet().getAssets()));
        changes.add(compareMetric("Liabilities", prev.getBalanceSheet().getLiabilities(), curr.getBalanceSheet().getLiabilities()));
        changes.add(compareMetric("Equity", prev.getBalanceSheet().getEquity(), curr.getBalanceSheet().getEquity()));

        return RealTimeMonitoringDTO.builder()
                .prevStartDate(prevStart)
                .prevEndDate(prevEnd)
                .currStartDate(currStart)
                .currEndDate(currEnd)
                .changes(changes)
                .currency(curr.getMetadata().getCurrency())
                .generatedBy(curr.getMetadata().getGeneratedBy())
                .generatedAt(LocalDateTime.now())
                .build();
    }

    private MetricChangeDTO compareMetric(String name, BigDecimal prev, BigDecimal curr) {
        prev = prev != null ? prev : BigDecimal.ZERO;
        curr = curr != null ? curr : BigDecimal.ZERO;

        BigDecimal change = curr.subtract(prev);
        BigDecimal changePercent = prev.compareTo(BigDecimal.ZERO) == 0
                ? (curr.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(100))
                : change.multiply(BigDecimal.valueOf(100)).divide(prev, 2, RoundingMode.HALF_UP);

        String trend = change.compareTo(BigDecimal.ZERO) > 0 ? "INCREASED"
                : change.compareTo(BigDecimal.ZERO) < 0 ? "DROPPED" : "STABLE";

        return MetricChangeDTO.builder()
                .metric(name)
                .previousValue(prev)
                .currentValue(curr)
                .changeAmount(change)
                .changePercentage(changePercent)
                .trend(trend)
                .build();
    }
}
