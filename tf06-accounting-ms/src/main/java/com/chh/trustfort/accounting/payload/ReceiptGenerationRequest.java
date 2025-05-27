package com.chh.trustfort.accounting.payload;

import com.chh.trustfort.accounting.enums.ReceiptSource;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReceiptGenerationRequest {
    private String payerName;
    private String payerEmail;
    private BigDecimal amount;
    private String currency;
    private String paymentReference;
    private ReceiptSource source;
    private String createdBy;
    private String businessUnit;   // ✅ add this
    private String department;
    private String accountCode; // e.g., "1000"

}