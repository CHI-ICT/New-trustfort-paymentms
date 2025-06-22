package com.chh.trustfort.accounting.dto;

import lombok.Data;

@Data
public class InstallmentRequestDto {
    private Long invoiceId;
    private int numberOfInstallments;
}
