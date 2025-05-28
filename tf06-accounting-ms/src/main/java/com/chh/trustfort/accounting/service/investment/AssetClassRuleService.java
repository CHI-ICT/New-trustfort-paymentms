package com.chh.trustfort.accounting.service.investment;

import com.chh.trustfort.accounting.dto.investment.InvestmentRequestDTO;
import com.chh.trustfort.accounting.enums.InsuranceProductType;
import com.chh.trustfort.accounting.model.AssetClass;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

@Service
public class AssetClassRuleService {

    public void validateInvestmentRules(InvestmentRequestDTO dto, AssetClass asset) {
        if (dto.getInsuranceProductType() == InsuranceProductType.LIFE
                && asset.getRiskLevel().equalsIgnoreCase("High")) {
            throw new IllegalArgumentException("LIFE insurance cannot invest in high-risk assets.");
        }

        long tenorYears = ChronoUnit.YEARS.between(dto.getStartDate(), dto.getMaturityDate());
        if (dto.getInsuranceProductType() == InsuranceProductType.LIFE && tenorYears < 10) {
            throw new IllegalArgumentException("Tenor must be at least 10 years for LIFE insurance.");
        }

        // Example of adding custom rule for Equities
        if (asset.getName().equalsIgnoreCase("Equities") && dto.getAmount().compareTo(new BigDecimal("1000000")) > 0) {
            throw new IllegalArgumentException("Equity investments are capped at 1 million.");
        }
    }
}

