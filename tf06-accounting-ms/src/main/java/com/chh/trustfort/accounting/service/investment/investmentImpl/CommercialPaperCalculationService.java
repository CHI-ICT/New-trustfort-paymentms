package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.model.CommercialPaperInvestment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;

@Service
public class CommercialPaperCalculationService {

    public void calculate(CommercialPaperInvestment cp) {
        // Ensure required fields are not null
        if (cp.getYieldRatePA() == null || cp.getPrincipal() == null || cp.getCommencementDate() == null || cp.getMaturityDate() == null) {
            throw new IllegalArgumentException("Required fields for calculation are missing.");
        }

        // 1. Calculate Tenor (in days)
        long tenor = ChronoUnit.DAYS.between(cp.getCommencementDate(), cp.getMaturityDate());
        cp.setTenor((Long) tenor);

        // 2. Interest = Principal * Yield Rate * Tenor / 365
        BigDecimal interest = cp.getPrincipal()
                .multiply(cp.getYieldRatePA())
                .multiply(BigDecimal.valueOf(tenor))
                .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
        cp.setInterest(interest);

        // 3. Adjustment (optional)
        BigDecimal adjustment = cp.getAdjustment() != null ? cp.getAdjustment() : BigDecimal.ZERO;

        // 4. Actual Interest = Interest + Adjustment
        BigDecimal actualInterest = interest.add(adjustment);
        cp.setActualInterest(actualInterest);

        // 5. Withholding Tax (WHT) = 10% of Actual Interest
        BigDecimal wht = actualInterest.multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP);
        cp.setWht(wht);

        // 6. Net Interest = Actual Interest - WHT
        BigDecimal netInterest = actualInterest.subtract(wht);
        cp.setNetInterest(netInterest);

        // 7. Final New Principal (optional logic)
        if (cp.getAdditionOrLiquidation() != null) {
            BigDecimal newPrincipal = cp.getPrincipal().add(cp.getAdditionOrLiquidation());
            cp.setNewPrincipal(newPrincipal);
        } else {
            cp.setNewPrincipal(cp.getPrincipal());
        }
    }
}

