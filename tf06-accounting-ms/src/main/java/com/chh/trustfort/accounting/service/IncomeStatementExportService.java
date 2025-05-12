package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.IncomeStatementResponse;
import com.chh.trustfort.accounting.dto.StatementFilterDTO;
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
public class IncomeStatementExportService {

    private final IncomeStatementService incomeStatementService;

    public byte[] exportToPdf(StatementFilterDTO filter) {
        IncomeStatementResponse data = incomeStatementService.generateIncomeStatement(filter);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("INCOME STATEMENT", titleFont));
            document.add(new Paragraph("Period: " + filter.getStartDate() + " to " + filter.getEndDate(), bodyFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Total Revenue: " + data.getTotalRevenue(), bodyFont));
            document.add(new Paragraph("Total Expenses: " + data.getTotalExpenses(), bodyFont));
            document.add(new Paragraph("Net Income: " + data.getNetIncome(), bodyFont));

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF export", e);
        }
    }

    public byte[] exportToExcel(StatementFilterDTO filter) {
        IncomeStatementResponse data = incomeStatementService.generateIncomeStatement(filter);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Income Statement");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Line Item");
            header.createCell(1).setCellValue("Amount");

            sheet.createRow(1).createCell(0).setCellValue("Total Revenue");
            sheet.getRow(1).createCell(1).setCellValue(data.getTotalRevenue().doubleValue());

            sheet.createRow(2).createCell(0).setCellValue("Total Expenses");
            sheet.getRow(2).createCell(1).setCellValue(data.getTotalExpenses().doubleValue());

            sheet.createRow(3).createCell(0).setCellValue("Net Income");
            sheet.getRow(3).createCell(1).setCellValue(data.getNetIncome().doubleValue());

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel export", e);
        }
    }
}
