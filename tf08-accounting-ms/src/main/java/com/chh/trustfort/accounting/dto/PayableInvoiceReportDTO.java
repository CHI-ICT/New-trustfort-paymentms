package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.enums.InvoiceStatus;
import com.chh.trustfort.accounting.enums.PayoutCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
public class PayableInvoiceReportDTO {

    private String invoiceNumber;
    private String vendorName;
    private String vendorEmail;
    private BigDecimal amount;
    private String currency;
    private InvoiceStatus status;
    private PayoutCategory payoutCategory;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    private boolean paid;

    public PayableInvoiceReportDTO(String invoiceNumber, String vendorName, String vendorEmail,
                                   BigDecimal amount, String currency,
                                   InvoiceStatus status, PayoutCategory payoutCategory,
                                   LocalDate dueDate, boolean paid) {
        this.invoiceNumber = invoiceNumber;
        this.vendorName = vendorName;
        this.vendorEmail = vendorEmail;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.payoutCategory = payoutCategory;
        this.dueDate = dueDate;
        this.paid = paid;
    }

}
