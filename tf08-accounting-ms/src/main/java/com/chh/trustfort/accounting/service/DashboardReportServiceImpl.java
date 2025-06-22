package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.DashboardSectionDTO;
import com.chh.trustfort.accounting.dto.DashboardSummaryDTO;
import com.chh.trustfort.accounting.dto.FinancialSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardReportServiceImpl implements DashboardReportService {

    private final FinancialSummaryService financialSummaryService;

    @Override
    public DashboardSummaryDTO generateDashboardView(LocalDate startDate, LocalDate endDate) {
        FinancialSummaryDTO summary = financialSummaryService.compileSummary(startDate, endDate);

        List<DashboardSectionDTO> sections = new ArrayList<>();

        // Revenue
        sections.add(buildSection("Revenue", summary.getIncomeStatement().getRevenue(), summary.getMetadata().getCurrency()));

        // Expenses
        sections.add(buildSection("Expenses", summary.getIncomeStatement().getExpenses(), summary.getMetadata().getCurrency()));

        // Net Profit
        sections.add(buildSection("Net Profit", summary.getIncomeStatement().getNetProfit(), summary.getMetadata().getCurrency()));

        // Assets
        sections.add(buildSection("Assets", summary.getBalanceSheet().getAssets(), summary.getMetadata().getCurrency()));

        // Liabilities
        sections.add(buildSection("Liabilities", summary.getBalanceSheet().getLiabilities(), summary.getMetadata().getCurrency()));

        // Equity
        sections.add(buildSection("Equity", summary.getBalanceSheet().getEquity(), summary.getMetadata().getCurrency()));

        return DashboardSummaryDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .sections(sections)
                .generatedBy(summary.getMetadata().getGeneratedBy())
                .generatedAt(summary.getMetadata().getGeneratedAt())
                .build();
    }

    private DashboardSectionDTO buildSection(String title, BigDecimal value, String currency) {
        String status = "NEUTRAL";
        if (value != null) {
            status = value.compareTo(BigDecimal.ZERO) > 0 ? "POSITIVE"
                    : value.compareTo(BigDecimal.ZERO) < 0 ? "NEGATIVE" : "NEUTRAL";
        }

        return DashboardSectionDTO.builder()
                .title(title)
                .value(value != null ? value : BigDecimal.ZERO)
                .currency(currency)
                .status(status)
                .build();
    }
}
