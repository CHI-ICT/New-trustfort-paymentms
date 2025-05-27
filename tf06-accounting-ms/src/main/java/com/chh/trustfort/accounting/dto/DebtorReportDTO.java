package com.chh.trustfort.accounting.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DebtorReportDTO {
    private String customerName;
    private String customerEmail;
    private BigDecimal totalInvoiced;
    private BigDecimal totalPaid;
    private BigDecimal outstandingAmount;
    private String currency;
    private String agingBucket; // e.g., "0-30 days", "31-60 days", etc.
}
