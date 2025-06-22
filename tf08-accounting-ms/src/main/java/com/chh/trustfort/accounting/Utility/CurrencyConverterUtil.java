package com.chh.trustfort.accounting.Utility;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyConverterUtil {
    public static BigDecimal convert(BigDecimal amount, BigDecimal rate) {
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
