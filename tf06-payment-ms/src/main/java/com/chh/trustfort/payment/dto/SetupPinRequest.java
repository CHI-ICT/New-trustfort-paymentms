package com.chh.trustfort.payment.dto;

import lombok.Data;

@Data
public class SetupPinRequest {
    private String rawPin;  // The plain PIN user submits (e.g., "1234")
}
