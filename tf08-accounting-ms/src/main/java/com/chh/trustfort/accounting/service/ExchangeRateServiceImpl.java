package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.component.LiveExchangeRateClient;
import com.chh.trustfort.accounting.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final LiveExchangeRateClient liveExchangeRateClient;
//    private static final Map<String, BigDecimal> rates = new HashMap<>();
//
//    static {
//        // Mock exchange rates to NGN
//        rates.put("USD", new BigDecimal("1350.00"));
//        rates.put("EUR", new BigDecimal("1450.00"));
//        rates.put("GBP", new BigDecimal("1700.00"));
//        rates.put("NGN", BigDecimal.ONE); // base currency
//    }

//    @Override
//    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
//        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
//            return BigDecimal.ONE;
//        }
//
//        if (!toCurrency.equalsIgnoreCase("NGN")) {
//            throw new UnsupportedOperationException("Only conversion to NGN supported for now.");
//        }
//
//        return rates.getOrDefault(fromCurrency.toUpperCase(), BigDecimal.ONE);
//    }

    @Override
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        try {
            return liveExchangeRateClient.getRate(fromCurrency, toCurrency);
        } catch (Exception e) {
            return fallbackRate(fromCurrency, toCurrency);
        }
    }

    private BigDecimal fallbackRate(String from, String to) {
        if (from.equalsIgnoreCase(to)) return BigDecimal.ONE;

        Map<String, BigDecimal> fallback = Map.of(
                "USD", new BigDecimal("1350.00"),
                "EUR", new BigDecimal("1450.00"),
                "GBP", new BigDecimal("1700.00"),
                "NGN", BigDecimal.ONE
        );

        return fallback.getOrDefault(from.toUpperCase(), BigDecimal.ONE);
    }
}
