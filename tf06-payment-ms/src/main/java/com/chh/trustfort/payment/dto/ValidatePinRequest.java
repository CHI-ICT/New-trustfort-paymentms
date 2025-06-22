package com.chh.trustfort.payment.dto;

import lombok.Data;

@Data
public class ValidatePinRequest {
    private String walletId;
    private String rawPin;
}
