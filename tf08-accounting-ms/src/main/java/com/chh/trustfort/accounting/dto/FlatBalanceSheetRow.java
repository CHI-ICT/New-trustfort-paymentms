package com.chh.trustfort.accounting.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class FlatBalanceSheetRow {
    private String groupCode;      // e.g., "A"
    private String groupName;      // e.g., "Cash and cash equivalents"
    private String accountCode;    // e.g., "8100102"
    private String accountName;    // e.g., "Petty Cash Account"
    private BigDecimal amount;     // e.g., 12500.00

    // Getters and setters...
}
