package com.chh.trustfort.accounting.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class IncomeStatementResponse {
    private BigDecimal totalRevenue;
    private BigDecimal totalExpenses;
    private BigDecimal netIncome;

    private List<ReportLineItem> revenueItems = new ArrayList<>(); // ✅ prevent null
    private List<ReportLineItem> expenseItems = new ArrayList<>();
}
