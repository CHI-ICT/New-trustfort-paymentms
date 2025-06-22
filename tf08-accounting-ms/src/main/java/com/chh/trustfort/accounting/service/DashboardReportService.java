package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.DashboardSummaryDTO;

import java.time.LocalDate;

public interface DashboardReportService {
    DashboardSummaryDTO generateDashboardView(LocalDate startDate, LocalDate endDate);
}
