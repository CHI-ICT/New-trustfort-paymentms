package com.chh.trustfort.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class PaystackWebhookPayload {
    private String event;
    private Data data;

    @lombok.Data
    public static class Data {
        private BigDecimal amount;
        private String reference;
        private Customer customer; // ✅ Correct nested customer object
        private Metadata metadata;
    }

    @lombok.Data
    public static class Customer {
        private String email; // ✅ Correct mapping from customer.email
    }

    @lombok.Data
    public static class Metadata {
        private String walletId;
        private Map<String, String> metadata;
    }
}
