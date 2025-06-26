package com.chh.trustfort.payment.dto;

import lombok.Data;

@Data
public class FlutterwavePaymentResponse {
    private String status;
    private String message;
    private String paymentLink;
}
