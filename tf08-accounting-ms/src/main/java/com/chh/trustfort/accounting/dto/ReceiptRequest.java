package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.enums.ReceiptSource;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReceiptRequest {
    private String payerName;
    private String payerEmail;
    private BigDecimal amount;
    private String currency;
    private String paymentReference;
    private ReceiptSource source;
}
