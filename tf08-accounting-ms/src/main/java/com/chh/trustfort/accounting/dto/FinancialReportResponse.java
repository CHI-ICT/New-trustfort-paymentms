package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.enums.ReportType;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class FinancialReportResponse {
    private String status;
    private String message;

    private ReportType reportType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private List<ReportLineItem> lineItems;
    private BigDecimal total;

    private List<CashFlowLineItemDTO> lineItem;
    private BigDecimal totalCashInflow;
    private BigDecimal totalCashOutflow;
    private BigDecimal netCashFlow;

    public static FinancialReportResponse fromBalanceSheet(BalanceSheetReportDTO dto) {
        return FinancialReportResponse.builder()
                .status("success")
                .message("Balance Sheet generated successfully")
                .reportType(ReportType.BALANCE_SHEET)
                .startDate(dto.getAsOfDate())
                .endDate(dto.getAsOfDate())
                .lineItems(null) // You can map dto.getLineItems() to ReportLineItem if needed
                .total(dto.getTotalAssets()) // Or use totalAssets + liabilities, depending on summary logic
                .build();
    }

}
