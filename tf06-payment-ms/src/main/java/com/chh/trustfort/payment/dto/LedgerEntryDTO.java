package com.chh.trustfort.payment.dto;

import com.chh.trustfort.payment.model.WalletLedgerEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntryDTO {

    private String walletId;
    private BigDecimal amount;
    private String transactionReference;
    private String narration;
    private String transactionType;
    private String status;
    private LocalDateTime transactionDate;
    private String sessionId;


    public static LedgerEntryDTO fromEntity(WalletLedgerEntry entry) {
        return new LedgerEntryDTO(
            entry.getWalletId(),
            entry.getAmount(),
            entry.getTransactionReference(),
            entry.getNarration(),
            entry.getTransactionType().name(),
            entry.getStatus().name(),
            entry.getCreatedAt(),
                entry.getSessionId()
        );
    }
}
