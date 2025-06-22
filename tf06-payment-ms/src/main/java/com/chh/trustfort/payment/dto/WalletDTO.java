package com.chh.trustfort.payment.dto;

import com.chh.trustfort.payment.enums.WalletStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletDTO {
    private String walletId;
    private String userId;
    private String email;
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private WalletStatus status;
}

