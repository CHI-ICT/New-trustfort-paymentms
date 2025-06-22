package com.chh.trustfort.payment.dto;

import com.chh.trustfort.payment.enums.CreditStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreditLineResponseDto {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private BigDecimal repaidAmount;
    private CreditStatus status;
    private String reason;
    private LocalDateTime requestedAt;
}

