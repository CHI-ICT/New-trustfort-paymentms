package com.chh.trustfort.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class ReportResponseDTO {
    private String reportTitle;
    private Map<String, Object> summary; // e.g., totals, ratios
    private Object data; // this can be a list of DTOs specific to each report
}
