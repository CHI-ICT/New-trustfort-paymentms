package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.Responses.ReportViewerResponse;
import com.chh.trustfort.accounting.dto.TrialBalanceResponse;

import java.time.LocalDate;
import java.util.List;

public interface TrialBalanceService {
    List<TrialBalanceResponse> generateTrialBalance(LocalDate startDate, LocalDate endDate);

    List<ReportViewerResponse> generateTrialBalanceForViewer(LocalDate startDate, LocalDate endDate);
}
