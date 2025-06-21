package com.chh.trustfort.payment.dto;

import lombok.Data;

@Data
public class GenerateAccountNumberRequest {
    private String walletId;
    private String fullName;
}
