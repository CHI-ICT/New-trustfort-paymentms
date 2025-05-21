package com.chh.trustfort.accounting.component;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class LiveExchangeRateClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_KEY = "6c8c6ef65d6f6736a221aaa0";

    public BigDecimal getRate(String base, String target) {
        String url = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/" + base.toUpperCase();

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> body = response.getBody();

        if (body != null && body.containsKey("conversion_rates")) {
            Map<String, Object> rates = (Map<String, Object>) body.get("conversion_rates");
            if (rates.containsKey(target.toUpperCase())) {
                Double rate = (Double) rates.get(target.toUpperCase());
                return BigDecimal.valueOf(rate);
            }
        }

        throw new RuntimeException("Failed to fetch live exchange rate for " + base + " to " + target);
    }
}
