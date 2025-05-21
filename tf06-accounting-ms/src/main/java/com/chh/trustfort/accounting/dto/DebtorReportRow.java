// --- DebtorReportRow.java ---
package com.chh.trustfort.accounting.dto;

import lombok.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DebtorReportRow {
    private String customerName;
    private String customerEmail;
    private BigDecimal totalAmount;
    private BigDecimal outstandingAmount;
    private LocalDate dueDate;
    private String status;
    private String currency;
}