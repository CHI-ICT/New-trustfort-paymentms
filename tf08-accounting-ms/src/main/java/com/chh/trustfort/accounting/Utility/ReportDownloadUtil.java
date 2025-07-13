package com.chh.trustfort.accounting.Utility;

import com.chh.trustfort.accounting.Responses.ReportViewerResponse;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.PageSize;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ReportDownloadUtil {

    public enum ExportFormat {
        PDF, EXCEL, CSV
    }

    public static Resource exportAsExcel(List<ReportViewerResponse> reportData) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Report");

            if (!reportData.isEmpty()) {
                Map<String, Object> firstRow = reportData.get(0).getFields();
                List<String> headers = new ArrayList<>(firstRow.keySet());

                Row companyRow = sheet.createRow(0);
                Cell companyCell = companyRow.createCell(0);
                companyCell.setCellValue("CONSOLIDATED HALLMARK INSURANCE PLC");

                Row headerRow = sheet.createRow(1);
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers.get(i));
                }

                for (int i = 0; i < reportData.size(); i++) {
                    Row row = sheet.createRow(i + 2);
                    List<Object> values = new ArrayList<>(reportData.get(i).getFields().values());
                    for (int j = 0; j < values.size(); j++) {
                        Cell cell = row.createCell(j);
                        cell.setCellValue(values.get(j) != null ? values.get(j).toString() : "");
                    }
                }
            }

            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        } catch (IOException e) {
            log.error("Failed to export report as Excel", e);
            return null;
        }
    }

    public static Resource exportAsCsv(List<ReportViewerResponse> reportData) {
        StringBuilder sb = new StringBuilder();

        if (!reportData.isEmpty()) {
            Map<String, Object> firstRow = reportData.get(0).getFields();
            List<String> headers = new ArrayList<>(firstRow.keySet());
            sb.append(String.join(",", headers)).append("\n");

            for (ReportViewerResponse row : reportData) {
                List<String> values = headers.stream()
                        .map(key -> {
                            Object val = row.getFields().get(key);
                            return val != null ? val.toString().replace(",", "") : "";
                        })
                        .collect(Collectors.toList());
                sb.append(String.join(",", values)).append("\n");
            }
        }

        return new ByteArrayResource(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static ByteArrayResource exportAsPdf(List<ReportViewerResponse> data, String reportTitle, LocalDate endDate, LocalDate date) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 14, Font.BOLD, BaseColor.RED);
            Font subTitleFont = new Font(baseFont, 12);
            Font headerFont = new Font(baseFont, 10, Font.BOLD);
            Font cellFont = new Font(baseFont, 9);

            Paragraph company = new Paragraph("CONSOLIDATED HALLMARK INSURANCE PLC", titleFont);
            company.setAlignment(Element.ALIGN_CENTER);
            company.setSpacingAfter(5f);
            document.add(company);

            Paragraph title = new Paragraph(reportTitle.toUpperCase(), headerFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5f);
            document.add(title);

            Paragraph dates = new Paragraph("AS AT " + endDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")), subTitleFont);
            dates.setAlignment(Element.ALIGN_CENTER);
            dates.setSpacingAfter(15f);
            document.add(dates);

            if (data.isEmpty()) throw new RuntimeException("No data to display");

            Map<String, Object> sample = data.get(0).getFields();
            String[] headers = sample.keySet().toArray(new String[0]);

            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);

            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5f);
                table.addCell(cell);
            }

            for (ReportViewerResponse row : data) {
                for (String h : headers) {
                    table.addCell(new Phrase(safe(row.getFields(), h), cellFont));
                }
            }

            document.add(table);

            Paragraph footer = new Paragraph("Run by: ICT    Print Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a")), subTitleFont);
            footer.setSpacingBefore(10f);
            footer.setAlignment(Element.ALIGN_LEFT);
            document.add(footer);

            document.close();
            return new ByteArrayResource(out.toByteArray());
        } catch (Exception ex) {
            throw new RuntimeException("PDF export failed", ex);
        }
    }

    private static String safe(Map<String, Object> map, String key) {
        return map.getOrDefault(key, "").toString();
    }
}