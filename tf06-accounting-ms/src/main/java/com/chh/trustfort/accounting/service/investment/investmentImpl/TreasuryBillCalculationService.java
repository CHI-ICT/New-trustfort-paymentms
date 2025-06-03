package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.model.TreasuryBillInvestment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TreasuryBillCalculationService {
    public void calculate(TreasuryBillInvestment tbill) {
        BigDecimal principal = tbill.getPrincipal();
        BigDecimal adjustment = tbill.getAdjustment() != null ? tbill.getAdjustment() : BigDecimal.ZERO;
        BigDecimal newPrincipal = principal.add(adjustment);
        BigDecimal rate = tbill.getYieldRatePA();
        Long tenor = tbill.getTenor();

        BigDecimal interest = newPrincipal.multiply(rate).multiply(BigDecimal.valueOf(tenor))
                .divide(BigDecimal.valueOf(100 * 365), 2, RoundingMode.HALF_UP);

        BigDecimal adjustedInterest = tbill.getAdjInterest() != null ? tbill.getAdjInterest() : BigDecimal.ZERO;
        BigDecimal actualInterest = interest.add(adjustedInterest);
        BigDecimal wht = actualInterest.multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal netInterest = actualInterest.subtract(wht);

        tbill.setNewPrincipal(newPrincipal);
        tbill.setInterest(interest);
        tbill.setActualInterest(actualInterest);
        tbill.setWithHoldingTax(wht);
        tbill.setNetInterest(netInterest);
    }
}
