package com.chh.trustfort.accounting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ERPExportDTO {
    private String reportType; // e.g. INCOME_STATEMENT or BALANCE_SHEET
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private Map<String, BigDecimal> values; // key-value financial indicators
    private String currency;
    private String exportedBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime exportedAt;
}
