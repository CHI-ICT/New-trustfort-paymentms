package com.chh.trustfort.payment.Responses;

import com.chh.trustfort.payment.model.Wallet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WalletResponse {
    private String responseCode;
    private String message;
    private Wallet wallet;

    // Getters and Setters
}

