package com.chh.trustfort.payment.service;

public interface FlutterwaveWebhookService {
    boolean handleWebhook(String payload);
}
