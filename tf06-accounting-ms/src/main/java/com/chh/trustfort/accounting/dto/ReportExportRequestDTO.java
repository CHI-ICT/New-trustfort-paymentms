package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.enums.ExportFormat;
import lombok.Data;

@Data
public class ReportExportRequestDTO {
    public String reportType;
    public ExportFormat format;
    public String requestedBy;
}

