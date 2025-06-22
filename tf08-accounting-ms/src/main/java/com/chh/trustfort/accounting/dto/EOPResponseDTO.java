package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

// EOPResponseDTO.java
@Data
@Builder
public class EOPResponseDTO {
    private Long eopId;
    private String vendorName;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private PaymentMethod paymentMethod;
    private String referenceNumber;
    private String downloadUrl;
}