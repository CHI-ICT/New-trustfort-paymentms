package com.chh.trustfort.accounting.service.investment;

import com.chh.trustfort.accounting.dto.ReportExportRequestDTO;

public interface InvestmentReportService {
    byte[] exportReport(ReportExportRequestDTO dto);
    void logExport(ReportExportRequestDTO dto);
}
