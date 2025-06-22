package com.chh.trustfort.accounting.dto;

import lombok.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankPaymentRequestDTO {
    private String beneficiaryName;
    private String beneficiaryAccountNumber;
    private String beneficiaryBankCode;
    private BigDecimal amount;
    private String narration;
    private String reference;
}
