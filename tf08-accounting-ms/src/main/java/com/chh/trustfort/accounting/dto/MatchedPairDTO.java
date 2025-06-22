package com.chh.trustfort.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchedPairDTO {
    private Long receiptId;
    private String reference;
    private BigDecimal matchedAmount;
    private String matchedWith;
}
