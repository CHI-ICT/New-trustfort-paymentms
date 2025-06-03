package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.model.EurobondInvestment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class EurobondCalculationService {

    public void calculate(EurobondInvestment eurobond) {
        BigDecimal cleanPrice = eurobond.getCleanPrice();
        BigDecimal accruedInterest = eurobond.getAccruedInterest();
        BigDecimal faceValue = eurobond.getFaceValue();
        BigDecimal exchangeRate = eurobond.getExchangeRate();

        BigDecimal dirtyPrice = cleanPrice.add(accruedInterest);
        eurobond.setDirtyPrice(dirtyPrice);

        BigDecimal amountPayable = dirtyPrice.multiply(faceValue).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        eurobond.setAmountPayable(amountPayable);

        eurobond.setPresentValue(amountPayable); // Can use DCF logic here if needed

        if (exchangeRate != null) {
            BigDecimal localValue = amountPayable.multiply(exchangeRate);
            eurobond.setLocalValue(localValue);
        }
    }
}

