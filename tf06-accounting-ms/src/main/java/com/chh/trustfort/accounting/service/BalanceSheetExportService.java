package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.BalanceSheetFilterRequest;
import com.chh.trustfort.accounting.dto.BalanceSheetResponse;
import com.chh.trustfort.accounting.service.BalanceSheetService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class BalanceSheetExportService {

    private final BalanceSheetService balanceSheetService;

    public byte[] exportToPdf(BalanceSheetFilterRequest filter) {
        BalanceSheetResponse data = balanceSheetService.generateBalanceSheet(filter);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("BALANCE SHEET", titleFont));
            document.add(new Paragraph("Period: " + filter.getStartDate() + " to " + filter.getEndDate(), bodyFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Total Assets: " + data.getTotalAssets(), bodyFont));
            document.add(new Paragraph("Total Liabilities: " + data.getTotalLiabilities(), bodyFont));
            document.add(new Paragraph("Total Equity: " + data.getTotalEquity(), bodyFont));
            document.add(new Paragraph("Cash at End: " + data.getCashAtEnd(), bodyFont));

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF export", e);
        }
    }

    public byte[] exportToExcel(BalanceSheetFilterRequest filter) {
        BalanceSheetResponse data = balanceSheetService.generateBalanceSheet(filter);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Balance Sheet");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Line Item");
            header.createCell(1).setCellValue("Amount");

            sheet.createRow(1).createCell(0).setCellValue("Total Assets");
            sheet.getRow(1).createCell(1).setCellValue(data.getTotalAssets().doubleValue());

            sheet.createRow(2).createCell(0).setCellValue("Total Liabilities");
            sheet.getRow(2).createCell(1).setCellValue(data.getTotalLiabilities().doubleValue());

            sheet.createRow(3).createCell(0).setCellValue("Total Equity");
            sheet.getRow(3).createCell(1).setCellValue(data.getTotalEquity().doubleValue());

            sheet.createRow(4).createCell(0).setCellValue("Cash at End");
            sheet.getRow(4).createCell(1).setCellValue(data.getCashAtEnd().doubleValue());

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel export", e);
        }
    }
}
