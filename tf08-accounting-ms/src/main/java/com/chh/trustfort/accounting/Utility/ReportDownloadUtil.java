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
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
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

//    public static ByteArrayResource exportAsPdf(List<ReportViewerResponse> data, String reportTitle, LocalDate endDate, LocalDate date) {
//        try {
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            Document document = new Document(PageSize.A4.rotate());
//            PdfWriter.getInstance(document, out);
//            document.open();
//
//            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
//            Font titleFont = new Font(baseFont, 14, Font.BOLD, BaseColor.RED);
//            Font subTitleFont = new Font(baseFont, 12);
//            Font headerFont = new Font(baseFont, 10, Font.BOLD);
//            Font cellFont = new Font(baseFont, 9);
//
//            Paragraph company = new Paragraph("CONSOLIDATED HALLMARK INSURANCE PLC", titleFont);
//            company.setAlignment(Element.ALIGN_CENTER);
//            company.setSpacingAfter(5f);
//            document.add(company);
//
//            Paragraph title = new Paragraph(reportTitle.toUpperCase(), headerFont);
//            title.setAlignment(Element.ALIGN_CENTER);
//            title.setSpacingAfter(5f);
//            document.add(title);
//
//            Paragraph dates = new Paragraph("AS AT " + endDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")), subTitleFont);
//            dates.setAlignment(Element.ALIGN_CENTER);
//            dates.setSpacingAfter(15f);
//            document.add(dates);
//
//            if (data.isEmpty()) throw new RuntimeException("No data to display");
//
//            Map<String, Object> sample = data.get(0).getFields();
//            String[] headers = sample.keySet().toArray(new String[0]);
//
//            PdfPTable table = new PdfPTable(headers.length);
//            table.setWidthPercentage(100);
//
//            for (String h : headers) {
//                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
//                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
//                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                cell.setPadding(5f);
//                table.addCell(cell);
//            }
//
//            for (ReportViewerResponse row : data) {
//                for (String h : headers) {
//                    table.addCell(new Phrase(safe(row.getFields(), h), cellFont));
//                }
//            }
//
//            document.add(table);
//
//            Paragraph footer = new Paragraph("Run by: ICT    Print Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a")), subTitleFont);
//            footer.setSpacingBefore(10f);
//            footer.setAlignment(Element.ALIGN_LEFT);
//            document.add(footer);
//
//            document.close();
//            return new ByteArrayResource(out.toByteArray());
//        } catch (Exception ex) {
//            throw new RuntimeException("PDF export failed", ex);
//        }
//    }

    private static String safe(Map<String, Object> map, String key) {
        return map.getOrDefault(key, "").toString();
    }

    public static ByteArrayResource exportAsPdf(List<ReportViewerResponse> data, String reportTitle, LocalDate endDate, LocalDate printDate) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 14, Font.BOLD, BaseColor.RED);
            Font subTitleFont = new Font(baseFont, 12);
            Font sectionFont = new Font(baseFont, 12, Font.BOLD, BaseColor.BLACK);
            Font headerFont = new Font(baseFont, 10, Font.BOLD, BaseColor.WHITE);
            Font cellFont = new Font(baseFont, 9);
            Font totalFont = new Font(baseFont, 10, Font.BOLD);

            DecimalFormat moneyFormat = new DecimalFormat("₦#,##0.00");

            // Header
            Paragraph company = new Paragraph("CONSOLIDATED HALLMARK INSURANCE PLC", titleFont);
            company.setAlignment(Element.ALIGN_CENTER);
            company.setSpacingAfter(5f);
            document.add(company);

            String formalTitle;
            if (reportTitle.toUpperCase().contains("EQUITY")) {
                formalTitle = "Statement of Changes in Equity for the Year Ended " + endDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
            } else if (reportTitle.toUpperCase().contains("CASH FLOW")) {
                formalTitle = "Statement of Cash Flows for the Year Ended " + endDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
            } else if (reportTitle.toUpperCase().contains("INCOME")) {
                formalTitle = "Income Statement for the Year Ended " + endDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
            } else {
                formalTitle = "Statement of Financial Position as at " + endDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
            }

            Paragraph title = new Paragraph(formalTitle, sectionFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10f);
            document.add(title);

            if (data.isEmpty()) throw new RuntimeException("No data to display");

            // ✅ EQUITY STATEMENT
            if (reportTitle.toUpperCase().contains("EQUITY")) {
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(60);
                table.setWidths(new float[]{6.0f, 3.0f});

                List<String> headers = List.of("Description", "Amount");
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                    cell.setBackgroundColor(BaseColor.GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5f);
                    table.addCell(cell);
                }

                for (ReportViewerResponse row : data) {
                    Map<String, Object> map = row.getFields();
                    String desc = safe(map, "Description");
                    BigDecimal amt = new BigDecimal(safe(map, "Amount"));

                    PdfPCell descCell = new PdfPCell(new Phrase(desc, cellFont));
                    PdfPCell amtCell = new PdfPCell(new Phrase(moneyFormat.format(amt), cellFont));
                    amtCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

                    if (desc.equalsIgnoreCase("Closing Equity")) {
                        descCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        amtCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        amtCell.setPhrase(new Phrase(moneyFormat.format(amt), totalFont));
                    }

                    table.addCell(descCell);
                    table.addCell(amtCell);
                }

                document.add(table);
            }

            // ✅ CASH FLOW STATEMENT
            else if (reportTitle.toUpperCase().contains("CASH FLOW")) {
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(60);
                table.setWidths(new float[]{6.0f, 3.0f});

                List<String> headers = List.of("Activity", "Amount");
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                    cell.setBackgroundColor(BaseColor.GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5f);
                    table.addCell(cell);
                }

                for (ReportViewerResponse row : data) {
                    Map<String, Object> map = row.getFields();
                    String activity = safe(map, "Activity");
                    BigDecimal amount = new BigDecimal(safe(map, "Amount"));

                    PdfPCell actCell = new PdfPCell(new Phrase(activity, cellFont));
                    PdfPCell amtCell = new PdfPCell(new Phrase(moneyFormat.format(amount), cellFont));
                    amtCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

                    if (activity.equalsIgnoreCase("Closing Balance")) {
                        actCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        amtCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        amtCell.setPhrase(new Phrase(moneyFormat.format(amount), totalFont));
                    }

                    table.addCell(actCell);
                    table.addCell(amtCell);
                }

                document.add(table);
            }

            // ✅ INCOME STATEMENT WITH CoA
            else if (reportTitle.toUpperCase().contains("INCOME")) {
                Map<String, List<ReportViewerResponse>> grouped = data.stream()
                        .collect(Collectors.groupingBy(r -> safe(r.getFields(), "section")));

                for (String section : List.of("REVENUE", "EXPENSE", "SUMMARY")) {
                    List<ReportViewerResponse> rows = grouped.get(section);
                    if (rows == null || rows.isEmpty()) continue;

                    Paragraph sectionHeader = new Paragraph(section + " SECTION", sectionFont);
                    sectionHeader.setSpacingBefore(10f);
                    sectionHeader.setSpacingAfter(5f);
                    document.add(sectionHeader);

                    PdfPTable table = new PdfPTable(5);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[]{3.5f, 6.0f, 2.5f, 5.0f, 3.0f});

                    List<String> headers = List.of("Section", "Group", "Account Code", "Account Name", "Amount");
                    for (String h : headers) {
                        PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                        cell.setBackgroundColor(BaseColor.GRAY);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setPadding(5f);
                        table.addCell(cell);
                    }

                    BigDecimal totalSection = BigDecimal.ZERO;

                    for (ReportViewerResponse row : rows) {
                        Map<String, Object> map = row.getFields();

                        String accountCode = safe(map, "accountCode");
                        String accountName = safe(map, "accountName");
                        BigDecimal amount = new BigDecimal(safe(map, "amount"));
                        totalSection = totalSection.add(amount);

                        table.addCell(new Phrase(safe(map, "section"), cellFont));
                        table.addCell(new Phrase(safe(map, "groupName"), cellFont));
                        table.addCell(new Phrase(accountCode, cellFont));
                        table.addCell(new Phrase(accountName, cellFont));

                        PdfPCell amountCell = new PdfPCell(new Phrase(moneyFormat.format(amount), cellFont));
                        amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(amountCell);
                    }

                    if (!section.equalsIgnoreCase("SUMMARY")) {
                        PdfPCell label = new PdfPCell(new Phrase("TOTAL " + section, totalFont));
                        label.setColspan(4);
                        label.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        label.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        table.addCell(label);

                        PdfPCell total = new PdfPCell(new Phrase(moneyFormat.format(totalSection), totalFont));
                        total.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        total.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        table.addCell(total);
                    }

                    document.add(table);
                }
            }

            // ✅ BALANCE SHEET & CONSOLIDATED
            else {
                Map<String, List<ReportViewerResponse>> grouped = data.stream()
                        .collect(Collectors.groupingBy(r -> safe(r.getFields(), "section")));

                int noteCounter = 1;

                for (String section : List.of("ASSET", "LIABILITY", "EQUITY")) {
                    List<ReportViewerResponse> rows = grouped.get(section);
                    if (rows == null || rows.isEmpty()) continue;

                    Paragraph sectionHeader = new Paragraph(section + " SECTION", sectionFont);
                    sectionHeader.setSpacingBefore(10f);
                    sectionHeader.setSpacingAfter(5f);
                    document.add(sectionHeader);

                    PdfPTable table = new PdfPTable(6);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[]{2.5f, 4.5f, 2.5f, 6.5f, 3.0f, 2.0f});

                    List<String> headers = List.of("Section", "Group", "Account Code", "Account Name", "Amount", "Note Ref");
                    for (String h : headers) {
                        PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                        cell.setBackgroundColor(BaseColor.GRAY);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setPadding(5f);
                        table.addCell(cell);
                    }

                    rows.sort(Comparator.comparing(r -> safe(r.getFields(), "accountCode")));
                    BigDecimal sectionTotal = BigDecimal.ZERO;

                    for (ReportViewerResponse row : rows) {
                        Map<String, Object> map = row.getFields();
                        table.addCell(new Phrase(safe(map, "section"), cellFont));
                        table.addCell(new Phrase(safe(map, "groupName"), cellFont));
                        table.addCell(new Phrase(safe(map, "accountCode"), cellFont));
                        table.addCell(new Phrase(safe(map, "accountName"), cellFont));

                        BigDecimal amt = new BigDecimal(safe(map, "amount"));
                        sectionTotal = sectionTotal.add(amt);

                        PdfPCell amountCell = new PdfPCell(new Phrase(moneyFormat.format(amt), cellFont));
                        amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table.addCell(amountCell);

                        PdfPCell noteCell = new PdfPCell(new Phrase("N" + noteCounter++, cellFont));
                        noteCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(noteCell);
                    }

                    PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL " + section, totalFont));
                    totalLabel.setColspan(4);
                    totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    totalLabel.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    totalLabel.setPadding(5f);

                    PdfPCell totalValue = new PdfPCell(new Phrase(moneyFormat.format(sectionTotal), totalFont));
                    totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    totalValue.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    totalValue.setPadding(5f);

                    PdfPCell totalNote = new PdfPCell(new Phrase("", totalFont));
                    totalNote.setBackgroundColor(BaseColor.LIGHT_GRAY);

                    table.addCell(totalLabel);
                    table.addCell(totalValue);
                    table.addCell(totalNote);

                    document.add(table);
                }
            }

            Paragraph footer = new Paragraph("Run by: ICT    Print Date: " + printDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), subTitleFont);
            footer.setSpacingBefore(10f);
            footer.setAlignment(Element.ALIGN_LEFT);
            document.add(footer);

            Paragraph disclaimer = new Paragraph("Prepared in accordance with IFRS. For internal management use only.", cellFont);
            disclaimer.setSpacingBefore(5f);
            disclaimer.setAlignment(Element.ALIGN_LEFT);
            document.add(disclaimer);

            document.close();
            return new ByteArrayResource(out.toByteArray());
        } catch (Exception ex) {
            throw new RuntimeException("PDF export failed", ex);
        }
    }



}