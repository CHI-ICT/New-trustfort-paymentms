// DTOs

package com.chh.trustfort.accounting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashflowForecastRequest {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}