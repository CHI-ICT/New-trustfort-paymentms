package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.TaxFilingSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxExportService {

    private final TaxFilingReportService taxFilingReportService;

    public byte[] exportTaxFilingReportToExcel(LocalDate startDate, LocalDate endDate) {
        List<TaxFilingSummaryDTO> summaries = taxFilingReportService.generateTaxFilingReport(startDate, endDate);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Tax Filing Report");

            int rowIdx = 0;

            // Header Row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.createCell(0).setCellValue("Tax Type");
            headerRow.createCell(1).setCellValue("Total Tax Amount");

            // Data Rows
            for (TaxFilingSummaryDTO summary : summaries) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(summary.getTaxType().name());
                BigDecimal totalTaxAmount = summary.getTotalTaxAmount() != null ? summary.getTotalTaxAmount() : BigDecimal.ZERO;
                row.createCell(1).setCellValue(totalTaxAmount.doubleValue());
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to export tax filing report to Excel", e);
        }
    }
}
