package com.chh.trustfort.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReconciliationSummaryDTO {
    private int totalReceivables;
    private int updatedRecords;
    private int paidCount;
    private int partiallyPaidCount;
    private int pendingCount;
    private String message;
}
