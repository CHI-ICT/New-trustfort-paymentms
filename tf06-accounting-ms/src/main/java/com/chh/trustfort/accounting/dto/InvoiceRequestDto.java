package com.chh.trustfort.accounting.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class InvoiceRequestDto {
    private Long userId;
    private BigDecimal amount;
    private String reference;
    private String description;
    private LocalDateTime dueDate;
}

