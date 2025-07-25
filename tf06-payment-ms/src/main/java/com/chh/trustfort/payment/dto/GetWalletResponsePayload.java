package com.chh.trustfort.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetWalletResponsePayload {
    private String responseCode;
    private String responseMessage;
    private List<WalletDTO> wallets;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WalletDTO {
        private String walletId;
        private String accountNumber;
        private String currency;
        private String balance;
        private String phoneNumber;
        private String email;
        private String userId;
        private String status;
    }
}
