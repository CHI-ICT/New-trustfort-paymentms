package com.chh.trustfort.accounting.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BankInflowPayload {
    private String reference;
    private String payerName;
    private BigDecimal amount;
    private String bankCode;
    private LocalDateTime paymentDate;
    private String remarks;
}
