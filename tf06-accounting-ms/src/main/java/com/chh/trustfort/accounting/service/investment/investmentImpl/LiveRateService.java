package com.chh.trustfort.accounting.service.investment.investmentImpl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class LiveRateService {

    private final String API_URL = "https://api.exchangerate.host/latest?base=USD";

    public BigDecimal getRate(String targetCurrency) {
        RestTemplate restTemplate = new RestTemplate();
        Map response = restTemplate.getForObject(API_URL, Map.class);
        Map<String, Double> rates = (Map<String, Double>) response.get("rates");

        Double rate = rates.getOrDefault(targetCurrency.toUpperCase(), 0.0);
        return BigDecimal.valueOf(rate);
    }
}

