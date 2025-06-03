package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.model.MoneyMarketInvestment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;

@Service
public class MoneyMarketCalculationService {

    private static final Logger logger = LoggerFactory.getLogger(MoneyMarketCalculationService.class);

    public void calculate(MoneyMarketInvestment investment, BigDecimal capitalizedInterestToDate) {
        // Validate required fields
        if (investment == null) {
            throw new IllegalArgumentException("Investment object must not be null");
        }

        if (investment.getRatePA() == null || investment.getNewPrincipal() == null) {
            logger.error("Invalid input: ratePA={}, newPrincipal={}", investment.getRatePA(), investment.getNewPrincipal());
            throw new IllegalArgumentException("RatePA and NewPrincipal must not be null");
        }

        if (investment.getValueDate() == null || investment.getEndOfPeriodDate() == null) {
            logger.error("Invalid input: valueDate={}, endOfPeriodDate={}", investment.getValueDate(), investment.getEndOfPeriodDate());
            throw new IllegalArgumentException("ValueDate and EndOfPeriodDate must not be null");
        }

        // Calculate tenor (days between start and end date)
        long tenor = ChronoUnit.DAYS.between(investment.getValueDate(), investment.getEndOfPeriodDate());
        investment.setTenor(tenor);

        BigDecimal rate = investment.getRatePA();
        BigDecimal principal = investment.getNewPrincipal();

        // Interest = Principal × Rate × Tenor / 36600
        BigDecimal interest = principal.multiply(rate)
                .multiply(BigDecimal.valueOf(tenor))
                .divide(BigDecimal.valueOf(36600), 2, RoundingMode.HALF_UP);

        investment.setInterest(interest);
        investment.setAdjustedInterest(BigDecimal.ZERO); // Assuming no adjustment logic for now
        investment.setActualInterest(interest);

        // WHT (10% of interest)
        BigDecimal wht = interest.multiply(BigDecimal.valueOf(0.1)).setScale(2, RoundingMode.HALF_UP);
        investment.setWithHoldingTax(wht);

        // Net Interest = Interest - WHT
        BigDecimal netInterest = interest.subtract(wht);
        investment.setNetInterest(netInterest);

        // Closing Principal = Principal + Net Interest
        BigDecimal closingPrincipal = principal.add(netInterest);
        investment.setClosingPrincipal(closingPrincipal);

        // Update capitalized interest to date
        BigDecimal updatedCapitalized = capitalizedInterestToDate != null
                ? capitalizedInterestToDate.add(interest)
                : interest;
        investment.setCapitalizedInterestToDate(updatedCapitalized);

        logger.info("Calculation completed for investment ID: {} - Principal: {}, Rate: {}, Interest: {}, NetInterest: {}",
                investment.getId(), principal, rate, interest, netInterest);
    }
}
