package com.chh.trustfort.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealTimeMonitoringDTO {
    private LocalDate prevStartDate;
    private LocalDate prevEndDate;
    private LocalDate currStartDate;
    private LocalDate currEndDate;
    private List<MetricChangeDTO> changes;
    private String currency;
    private String generatedBy;
    private LocalDateTime generatedAt;
}
