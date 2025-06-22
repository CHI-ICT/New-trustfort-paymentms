package com.chh.trustfort.payment.dto;

import com.chh.trustfort.payment.enums.PaymentGateway;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InitiatePaymentRequest {
    private String email;
    private BigDecimal amount;
    private String walletId;
    private PaymentGateway gateway;
}
