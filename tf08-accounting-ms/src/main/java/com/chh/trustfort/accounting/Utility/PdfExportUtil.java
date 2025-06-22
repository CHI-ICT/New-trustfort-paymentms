//package com.chh.trustfort.accounting.Utility;
//
//import com.chh.trustfort.accounting.Responses.ReportViewerResponse;
//import com.itextpdf.kernel.colors.Color;
//import com.itextpdf.text.*;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfWriter;
//
//import java.io.ByteArrayOutputStream;
//import java.util.List;
//import java.util.Set;
//
//public class PdfExportUtil {
//
//    public static byte[] exportReportToPdf(List<ReportViewerResponse> reportData, String reportTitle, String runBy, String period) {
//        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//            Document document = new Document();
//            PdfWriter.getInstance(document, out);
//            document.open();
//
//            // Title
//            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
//            Paragraph title = new Paragraph(reportTitle.toUpperCase(), titleFont);
//            title.setAlignment(Element.ALIGN_CENTER);
//            document.add(title);
//
//            // Metadata
//            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
//            document.add(new Paragraph("Run by: " + runBy, metaFont));
//            document.add(new Paragraph("Period: " + period, metaFont));
//            document.add(Chunk.NEWLINE);
//
//            // Extract dynamic headers
//            Set<String> headers = reportData.isEmpty() ? Set.of("Metric", "Amount") : reportData.get(0).getFields().keySet();
//
//            // Table
//            PdfPTable table = new PdfPTable(headers.size());
//            table.setWidthPercentage(100f);
//
//            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
//            Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
//
//            // Add headers
//            for (String header : headers) {
//                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
//                cell.setBackgroundColor(Color.LIGHT_GRAY);
//                table.addCell(cell);
//            }
//
//            // Add rows
//            for (ReportViewerResponse row : reportData) {
//                for (String header : headers) {
//                    Object value = row.getFields().getOrDefault(header, "");
//                    table.addCell(new Phrase(String.valueOf(value), rowFont));
//                }
//            }
//
//            document.add(table);
//            document.close();
//            return out.toByteArray();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to export PDF", e);
//        }
//    }
//}
