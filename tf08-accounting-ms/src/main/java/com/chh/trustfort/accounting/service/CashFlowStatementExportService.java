package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.CashFlowStatementDTO;
import com.chh.trustfort.accounting.dto.StatementFilterDTO;
import com.chh.trustfort.accounting.service.CashFlowStatementService;
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
public class CashFlowStatementExportService {

    private final CashFlowStatementService cashFlowStatementService;

    public byte[] exportToPdf(StatementFilterDTO filter) {
        CashFlowStatementDTO data = cashFlowStatementService.generateCashFlowStatement(filter);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("CASH FLOW STATEMENT", titleFont));
            document.add(new Paragraph("Period: " + filter.getStartDate() + " to " + filter.getEndDate(), bodyFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Operating Activities: " + data.getOperatingActivities(), bodyFont));
            document.add(new Paragraph("Investing Activities: " + data.getInvestingActivities(), bodyFont));
            document.add(new Paragraph("Financing Activities: " + data.getFinancingActivities(), bodyFont));
            document.add(new Paragraph("Net Cash Flow: " + data.getNetCashFlow(), bodyFont));
            document.add(new Paragraph("Opening Cash Balance: " + data.getOpeningCashBalance(), bodyFont));
            document.add(new Paragraph("Closing Cash Balance: " + data.getClosingCashBalance(), bodyFont));

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Cash Flow PDF", e);
        }
    }

    public byte[] exportToExcel(StatementFilterDTO filter) {
        CashFlowStatementDTO data = cashFlowStatementService.generateCashFlowStatement(filter);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Cash Flow Statement");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Line Item");
            header.createCell(1).setCellValue("Amount");

            sheet.createRow(1).createCell(0).setCellValue("Operating Activities");
            sheet.getRow(1).createCell(1).setCellValue(data.getOperatingActivities().doubleValue());

            sheet.createRow(2).createCell(0).setCellValue("Investing Activities");
            sheet.getRow(2).createCell(1).setCellValue(data.getInvestingActivities().doubleValue());

            sheet.createRow(3).createCell(0).setCellValue("Financing Activities");
            sheet.getRow(3).createCell(1).setCellValue(data.getFinancingActivities().doubleValue());

            sheet.createRow(4).createCell(0).setCellValue("Net Cash Flow");
            sheet.getRow(4).createCell(1).setCellValue(data.getNetCashFlow().doubleValue());

            sheet.createRow(5).createCell(0).setCellValue("Opening Cash Balance");
            sheet.getRow(5).createCell(1).setCellValue(data.getOpeningCashBalance().doubleValue());

            sheet.createRow(6).createCell(0).setCellValue("Closing Cash Balance");
            sheet.getRow(6).createCell(1).setCellValue(data.getClosingCashBalance().doubleValue());

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Cash Flow Excel", e);
        }
    }
}
