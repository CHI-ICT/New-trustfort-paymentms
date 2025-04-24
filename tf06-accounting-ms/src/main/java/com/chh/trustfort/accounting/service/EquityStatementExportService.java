package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.EquityStatementResponse;
import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.service.EquityStatementService;
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
public class EquityStatementExportService {

    private final EquityStatementService equityStatementService;

    public byte[] exportToPdf(StatementFilterDTO filter) {
        EquityStatementResponse data = equityStatementService.generateStatement(filter);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("STATEMENT OF EQUITY", titleFont));
            document.add(new Paragraph("Period: " + filter.getStartDate() + " to " + filter.getEndDate(), bodyFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Opening Equity: " + data.getOpeningEquity(), bodyFont));
            document.add(new Paragraph("Contributions: " + data.getContributions(), bodyFont));
            document.add(new Paragraph("Retained Earnings: " + data.getRetainedEarnings(), bodyFont));
            document.add(new Paragraph("Dividends: " + data.getDividends(), bodyFont));
            document.add(new Paragraph("Closing Equity: " + data.getClosingEquity(), bodyFont));

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Equity Statement PDF", e);
        }
    }

    public byte[] exportToExcel(StatementFilterDTO filter) {
        EquityStatementResponse data = equityStatementService.generateStatement(filter);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Equity Statement");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Line Item");
            header.createCell(1).setCellValue("Amount");

            sheet.createRow(1).createCell(0).setCellValue("Opening Equity");
            sheet.getRow(1).createCell(1).setCellValue(data.getOpeningEquity().doubleValue());

            sheet.createRow(2).createCell(0).setCellValue("Contributions");
            sheet.getRow(2).createCell(1).setCellValue(data.getContributions().doubleValue());

            sheet.createRow(3).createCell(0).setCellValue("Retained Earnings");
            sheet.getRow(3).createCell(1).setCellValue(data.getRetainedEarnings().doubleValue());

            sheet.createRow(4).createCell(0).setCellValue("Dividends");
            sheet.getRow(4).createCell(1).setCellValue(data.getDividends().doubleValue());

            sheet.createRow(5).createCell(0).setCellValue("Closing Equity");
            sheet.getRow(5).createCell(1).setCellValue(data.getClosingEquity().doubleValue());

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Equity Statement Excel", e);
        }
    }
}
