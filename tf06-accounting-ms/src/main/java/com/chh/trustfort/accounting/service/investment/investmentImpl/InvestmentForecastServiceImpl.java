package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.dto.investment.InvestmentForecastProjectionDTO;
import com.chh.trustfort.accounting.model.Investment;
import com.chh.trustfort.accounting.repository.InvestmentRepository;
import com.chh.trustfort.accounting.service.investment.InvestmentForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class InvestmentForecastServiceImpl implements InvestmentForecastService {

    @Autowired
    private InvestmentRepository investmentRepository;

    @Override
    public InvestmentForecastProjectionDTO forecast(Long investmentId) {
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new IllegalArgumentException("Investment not found"));

        InvestmentForecastProjectionDTO dto = new InvestmentForecastProjectionDTO();
        dto.investmentId = investmentId;
        dto.projectionDate = LocalDate.now().plusMonths(6);

        // Simple linear forecast example
        BigDecimal annualRate = investment.getAssetClass().getAverageReturnRate();
        BigDecimal forecast = investment.getAmount()
                .multiply(annualRate)
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP); // 6 months forecast

        dto.projectedReturn = forecast;
        return dto;
    }
}

