package com.chh.trustfort.accounting.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LinkDebitNoteRequest {
    private Long oldDebitNoteId;
    private String payerEmail;
    private String customerName;
    private BigDecimal amount;
    private String currency;
    private LocalDate dueDate;
    private String createdBy;
}
