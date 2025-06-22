package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.ERPExportDTO;
import org.springframework.http.ResponseEntity;

import javax.annotation.Resource;
import java.time.LocalDate;

public interface ReportExportService {
    ERPExportDTO prepareExport(String reportType, LocalDate startDate, LocalDate endDate);

    ResponseEntity<Resource> exportReport(String reportType, String format);
}
