package com.chh.trustfort.accounting.Utility;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class PageNumberEvent extends PdfPageEventHelper {
    private PdfTemplate totalPages;

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        totalPages = writer.getDirectContent().createTemplate(50, 50);
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8);
        Phrase footer = new Phrase("Page " + writer.getPageNumber() + " of ", footerFont);

        float x = (document.right() + document.left()) / 2;
        float y = document.bottom() - 10;

        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, x, y, 0);
        cb.addTemplate(totalPages, x + 25, y); // reserve space for total pages
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        String totalPageCount = String.valueOf(writer.getPageNumber() - 1);
        Font font = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8);
        Phrase total = new Phrase(totalPageCount, font);
        ColumnText.showTextAligned(totalPages, Element.ALIGN_LEFT, total, 0, 0, 0);
    }
}
