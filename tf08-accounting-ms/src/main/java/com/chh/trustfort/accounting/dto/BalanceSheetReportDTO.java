// DTO to wrap the full balance sheet report
package com.chh.trustfort.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceSheetReportDTO {
    private LocalDate asOfDate;
    private BigDecimal totalAssets;
    private BigDecimal totalLiabilities;
    private BigDecimal totalEquity;
    private BigDecimal totalLiabilitiesAndEquity;
    private List<BalanceSheetLineItemDTO> lineItems;
}

