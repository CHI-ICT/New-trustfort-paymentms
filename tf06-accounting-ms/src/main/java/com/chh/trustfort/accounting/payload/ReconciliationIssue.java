package com.chh.trustfort.accounting.payload;

import lombok.Data;

@Data
public class ReconciliationIssue {
    private String accountCode;
    private String issue;
}
