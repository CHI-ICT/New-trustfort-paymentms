package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.enums.ReportType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportRequestDTO {
    private ReportType reportType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String departmentCode;
    private String businessUnit;
    private String currency; // optional for multi-currency setup
}
