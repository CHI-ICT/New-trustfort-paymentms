package com.chh.trustfort.accounting.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DownloadReportRequestDTO {
    private String reportType;   // e.g., "trial_balance", "cash_flow"
    private String format;       // e.g., "pdf", "excel", "csv"
    private LocalDate startDate;
    private LocalDate endDate;
}
