package com.chh.trustfort.accounting.service.investment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ReturnCalculationService {
    public BigDecimal calculateExpectedReturn(BigDecimal amount, BigDecimal annualRate, LocalDate start, LocalDate end) {
        long days = ChronoUnit.DAYS.between(start, end);
        BigDecimal ratePerDay = annualRate.divide(BigDecimal.valueOf(365), 6, RoundingMode.HALF_UP);
        return amount.multiply(ratePerDay.multiply(BigDecimal.valueOf(days))).setScale(2, RoundingMode.HALF_UP);
    }
}
