package com.chh.trustfort.accounting.payload;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TrialBalanceResponse {
    private List<TrialBalanceDTO> entries;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private boolean isBalanced;
    private List<ReconciliationIssue> reconciliationIssues;
}
