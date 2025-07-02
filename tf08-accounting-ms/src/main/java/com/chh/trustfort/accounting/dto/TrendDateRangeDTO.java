package com.chh.trustfort.accounting.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TrendDateRangeDTO {
    private LocalDate prevStart;
    private LocalDate prevEnd;
    private LocalDate currStart;
    private LocalDate currEnd;
}
