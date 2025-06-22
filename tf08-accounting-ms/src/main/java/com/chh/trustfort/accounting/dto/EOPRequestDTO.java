package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.enums.PaymentMethod;
import lombok.Data;

// EOPRequestDTO.java
@Data
public class EOPRequestDTO {
    private PaymentMethod paymentMethod;
    private String referenceNumber;
    private String generatedBy;
}
