package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.dto.InvestmentCalculationResultDTO;
import com.chh.trustfort.accounting.enums.InvestmentSubtype;
import com.chh.trustfort.accounting.model.Investment;
import com.chh.trustfort.accounting.service.investment.InvestmentCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.chh.trustfort.accounting.enums.InvestmentSubtype.NAIRA;

@Service
public class InvestmentCalculationServiceImpl implements InvestmentCalculationService {
    @Autowired
    private LiveRateService liveRateService;


    public InvestmentCalculationResultDTO calculate(InvestmentSubtype subtype, BigDecimal amount, double tenorYears) {
        BigDecimal rate = getRateBySubtype(subtype);
        BigDecimal interest = amount.multiply(rate).multiply(BigDecimal.valueOf(tenorYears)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedReturn = amount.add(interest);

        return new InvestmentCalculationResultDTO(
                rate.setScale(4, RoundingMode.HALF_UP),
                interest,
                BigDecimal.ZERO,
                expectedReturn
        );
    }

    private BigDecimal getRateBySubtype(InvestmentSubtype subtype) {
        switch (subtype) {
            case DOLLAR:
                return liveRateService.getRate("USD");
            case NAIRA:
                return BigDecimal.ONE;
            case EUROBOND:
                return new BigDecimal("0.07625");
            case CORPORATE_AND_GOVT_BOND:
                return new BigDecimal("0.17046");
            case QUOTED_EQUITY:
            case UNQUOTED_EQUITY:
                return new BigDecimal("0.15");
            default:
                throw new IllegalArgumentException("Unsupported subtype: " + subtype);
        }
    }
}

