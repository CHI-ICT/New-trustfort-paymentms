package com.chh.trustfort.accounting.service.investment;

import com.chh.trustfort.accounting.dto.InvestmentCalculationResultDTO;

import java.math.BigDecimal;

public interface InvestmentCalculationService {
    InvestmentCalculationResultDTO calculate(Long assetClassId, BigDecimal amount, BigDecimal tenorYears);
}
