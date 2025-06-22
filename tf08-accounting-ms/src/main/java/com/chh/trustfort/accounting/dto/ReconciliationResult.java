package com.chh.trustfort.accounting.dto;

import lombok.*;
import lombok.Data;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationResult {
    private List<String> reconciledInvoices;
    private List<String> discrepancies;
}
