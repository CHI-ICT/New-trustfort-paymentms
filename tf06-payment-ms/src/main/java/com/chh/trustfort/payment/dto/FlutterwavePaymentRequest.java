package com.chh.trustfort.payment.dto;

import lombok.Data;

@Data
public class FlutterwavePaymentRequest {
    private String amount;
    private String currency;
    private String userId;         // Phone or Email used as wallet ID
    private String email;
    private String phoneNumber;
    private String name;
    private String redirectUrl;    // Optional, or use a default if not provided
}
