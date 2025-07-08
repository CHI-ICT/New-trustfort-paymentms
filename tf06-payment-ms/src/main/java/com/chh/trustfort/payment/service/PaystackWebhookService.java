package com.chh.trustfort.payment.service;

public interface PaystackWebhookService {
    boolean handleWebhook(String payload);
}
