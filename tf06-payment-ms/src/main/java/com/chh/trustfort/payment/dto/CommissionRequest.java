package com.chh.trustfort.payment.dto;

import com.chh.trustfort.payment.enums.CommissionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommissionRequest {
    private CommissionType commissionType;
    private BigDecimal amount;
    private String walletId;
}


