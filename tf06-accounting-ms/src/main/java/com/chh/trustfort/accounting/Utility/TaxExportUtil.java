package com.chh.trustfort.accounting.Utility;

import com.chh.trustfort.accounting.dto.TaxFilingSummaryDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class TaxExportUtil {

    /**
     * Export tax filing summaries to Excel.
     */
    public static byte[] exportToExcel(List<TaxFilingSummaryDTO> taxSummaries) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Tax Filing Report");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Tax Type");
        headerRow.createCell(1).setCellValue("Total Amount");

        int rowIdx = 1;
        for (TaxFilingSummaryDTO dto : taxSummaries) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(dto.getTaxType().name());
            row.createCell(1).setCellValue(dto.getTotalTaxAmount().doubleValue());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    /**
     * Export tax filing summaries to CSV.
     */
    public static String exportToCSV(List<TaxFilingSummaryDTO> taxSummaries) {
        StringBuilder builder = new StringBuilder();
        builder.append("Tax Type,Total Amount\n");

        for (TaxFilingSummaryDTO dto : taxSummaries) {
            builder.append(dto.getTaxType().name()).append(",");
            builder.append(dto.getTotalTaxAmount()).append("\n");
        }

        return builder.toString();
    }
}
