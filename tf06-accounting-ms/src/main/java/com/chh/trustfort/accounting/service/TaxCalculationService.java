package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.enums.TaxType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaxCalculationService {

    private static final Map<TaxType, BigDecimal> TAX_RATES = new HashMap<>();

    static {
        TAX_RATES.put(TaxType.VAT, new BigDecimal("7.5"));           // VAT = 7.5%
        TAX_RATES.put(TaxType.WHT, new BigDecimal("5"));             // WHT = 5%
        TAX_RATES.put(TaxType.PAYE, new BigDecimal("10"));           // PAYE = 10% (example)
        TAX_RATES.put(TaxType.COMPANY_INCOME_TAX, new BigDecimal("30")); // CIT = 30% (example)
        TAX_RATES.put(TaxType.EDUCATION_TAX, new BigDecimal("2"));    // Education Tax = 2%
        TAX_RATES.put(TaxType.IT_TAX, new BigDecimal("1"));           // IT Tax = 1%
    }

    /**
     * Calculate the tax amount based on a transaction base amount and tax type.
     * @param baseAmount The transaction amount (before tax)
     * @param taxType The type of tax to calculate
     * @return The tax amount (BigDecimal)
     */
    public BigDecimal calculateTax(BigDecimal baseAmount, TaxType taxType) {
        BigDecimal rate = TAX_RATES.get(taxType);
        if (rate == null) {
            throw new IllegalArgumentException("Unknown or unsupported Tax Type: " + taxType);
        }

        // Tax = (Rate / 100) * Base Amount
        return baseAmount.multiply(rate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * Retrieve the tax rate for a given tax type.
     */
    public BigDecimal getTaxRate(TaxType taxType) {
        return TAX_RATES.get(taxType);
    }
}
