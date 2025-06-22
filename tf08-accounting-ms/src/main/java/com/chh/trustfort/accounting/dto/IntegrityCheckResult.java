package com.chh.trustfort.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntegrityCheckResult {
    private String ruleCode;
    private String description;
    private boolean passed;
    private String message;
}
