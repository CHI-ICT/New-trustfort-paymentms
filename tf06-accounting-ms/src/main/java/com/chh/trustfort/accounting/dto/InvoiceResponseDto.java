package com.chh.trustfort.accounting.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InvoiceResponseDto {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private String reference;
    private String description;
    private String status;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
}

