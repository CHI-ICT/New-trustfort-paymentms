package com.chh.trustfort.accounting.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditNoteRequestDTO {
    private String reference;
    private BigDecimal amount;
    private String currency;
    private String description; // maps to remarks
    private Long linkedDebitNoteId;
    private String createdBy;
}