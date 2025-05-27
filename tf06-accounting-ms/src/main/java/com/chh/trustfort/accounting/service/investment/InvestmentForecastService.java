package com.chh.trustfort.accounting.service.investment;

import com.chh.trustfort.accounting.dto.investment.InvestmentForecastProjectionDTO;

public interface InvestmentForecastService {
    InvestmentForecastProjectionDTO forecast(Long investmentId);
}
