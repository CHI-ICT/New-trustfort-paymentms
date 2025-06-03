package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.model.CorporateAndGovtBondInvestment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CorporateAndGovtBondCalculationService {

    public void calculate(CorporateAndGovtBondInvestment bond) {
        BigDecimal dailyAccrual = bond.getDailyAccrual();

        int daysInPeriod = bond.getDaysInPeriod();

        BigDecimal interestReceivable = dailyAccrual.multiply(BigDecimal.valueOf(daysInPeriod));
        bond.setInterestReceivable(interestReceivable);

        BigDecimal amortizedCost = bond.getCostPerUnit().multiply(bond.getVolume());
        bond.setAmortisedCost(amortizedCost);

        BigDecimal marketValue = bond.getMarketPrice().multiply(bond.getVolume());
        bond.setMarketValue(marketValue);

        BigDecimal unrealizedGainLoss = marketValue.subtract(amortizedCost);
        bond.setUnrealizedGainLoss(unrealizedGainLoss);

        BigDecimal cvWithInterest = amortizedCost.add(interestReceivable);
        bond.setCarryingValueWithInterest(cvWithInterest);
    }
}

