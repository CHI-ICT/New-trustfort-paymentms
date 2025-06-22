package com.chh.trustfort.accounting.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FinancialSummaryDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private SummarySection incomeStatement;
    private SummarySection balanceSheet;
    private SummaryMetadata metadata;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SummarySection {
        private BigDecimal revenue;
        private BigDecimal expenses;
        private BigDecimal netProfit;
        private BigDecimal assets;
        private BigDecimal liabilities;
        private BigDecimal equity;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SummaryMetadata {
        private String currency = "NGN";
        private String generatedBy;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDateTime generatedAt;
    }
}
