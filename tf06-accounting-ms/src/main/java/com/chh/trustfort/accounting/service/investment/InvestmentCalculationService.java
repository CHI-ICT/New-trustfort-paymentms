package com.chh.trustfort.accounting.service.investment;

import com.chh.trustfort.accounting.dto.InvestmentCalculationResultDTO;
import com.chh.trustfort.accounting.enums.InvestmentSubtype;

import java.math.BigDecimal;

public interface InvestmentCalculationService {
    InvestmentCalculationResultDTO calculate(InvestmentSubtype subtype, BigDecimal amount, double tenorYears);
}
