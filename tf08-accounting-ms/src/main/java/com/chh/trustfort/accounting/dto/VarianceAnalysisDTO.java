package com.chh.trustfort.accounting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// VarianceAnalysisDTO.java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VarianceAnalysisDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private List<VarianceResultDTO> results;
    private String currency;
    private String generatedBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime generatedAt;
}
