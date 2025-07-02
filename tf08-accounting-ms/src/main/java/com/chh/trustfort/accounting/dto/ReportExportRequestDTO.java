package com.chh.trustfort.accounting.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReportExportRequestDTO {
    private String reportType;
    private String format; // e.g. PDF, EXCEL, CSV
    private LocalDate startDate;
    private LocalDate endDate;
}
