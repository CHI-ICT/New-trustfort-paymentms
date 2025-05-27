package com.chh.trustfort.accounting.dto.investment;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvestmentForecastProjectionDTO {
    public Long investmentId;
    public LocalDate projectionDate;
    public BigDecimal projectedReturn;
}

