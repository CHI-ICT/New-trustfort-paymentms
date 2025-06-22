package com.chh.trustfort.payment.component;

import com.chh.trustfort.payment.dto.JournalEntryRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AccountingClient {

    private final RestTemplate restTemplate;

    @Value("${accounting.service.url}") // e.g., http://localhost:1006/trustfort/api/v1
    private String accountingBaseUrl;

    public AccountingClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public void postJournalEntry(JournalEntryRequest request) {
        String url = accountingBaseUrl + "/journal-entry"; // update if your endpoint path differs
        restTemplate.postForObject(url, request, Void.class);
    }
}
