package com.chh.trustfort.payment.dto;


import lombok.Data;

@Data
public class GenerateAccountNumberResponse {
    private String accountNumber;
    private String walletId;
    private String status;
}