package com.chh.trustfort.payment.dto;

import lombok.Data;

@Data
public class WebhookPayload {
    private String walletId;
    private String transferStatus; // SUCCESS or FAILED
    private double amount;
}
