package com.chh.trustfort.accounting.dto;

import lombok.*;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@Data
@Builder
public class BalanceSheetLineItemDTO {
    private String section; // "ASSETS", "LIABILITIES", "EQUITY"
    private String groupCode;
    private String groupName;
    private String accountCode;
    private String accountName;
    private BigDecimal amount;
//    private String groupCode;
//    private String groupName;
//    private String accountCode;
//    private String accountName;
//    private BigDecimal amount;

}

