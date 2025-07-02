package com.chh.trustfort.accounting.payload;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BankInflowPayload {
    private String reference;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime receivedAt;
    private String payerName;
    private String payerEmail;
}
