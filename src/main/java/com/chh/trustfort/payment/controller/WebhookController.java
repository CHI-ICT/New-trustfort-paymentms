package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.WebhookPayload;
import com.chh.trustfort.payment.enums.TransactionStatus;
import com.chh.trustfort.payment.exception.WalletException;
import com.chh.trustfort.payment.model.LedgerEntry;
import com.chh.trustfort.payment.model.SettlementAccount;
import com.chh.trustfort.payment.model.Wallet;
import com.chh.trustfort.payment.repository.LedgerEntryRepository;
import com.chh.trustfort.payment.repository.SettlementAccountRepository;
import com.chh.trustfort.payment.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
public class WebhookController {

    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final SettlementAccountRepository settlementAccountRepository;


    @PostMapping(value = ApiPath.HANDLE_FCMB_WEBHOOK, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleFcmbWebhook(@RequestBody WebhookPayload payload) {
        log.info("📨 Received webhook for wallet {} with status {}", payload.getWalletId(), payload.getTransferStatus());

        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
                .orElseThrow(() -> new WalletException("Wallet not found"));

        LedgerEntry ledgerEntry = ledgerEntryRepository.findPendingByWalletId(wallet.getWalletId())
                .orElseThrow(() -> new WalletException("No pending ledger found"));

        BigDecimal amount = BigDecimal.valueOf(payload.getAmount());

        if ("SUCCESS".equalsIgnoreCase(payload.getTransferStatus())) {
            ledgerEntry.setStatus(TransactionStatus.COMPLETED);
            log.info("✅ Ledger updated to COMPLETED");
        } else {
            // Rollback balance
            wallet.setBalance(wallet.getBalance().add(amount));
            walletRepository.updateUser(wallet);

            ledgerEntry.setStatus(TransactionStatus.FAILED);
            log.warn("❌ Transfer failed. Wallet refunded");
        }

        ledgerEntryRepository.save(ledgerEntry);

        // Update settlement account
        SettlementAccount account = settlementAccountRepository.findByAccountNumber("MOCK-FCMB-001");
        if (account != null) {
            account.setBalance(account.getBalance().subtract(amount));
            settlementAccountRepository.save(account);
        }

        return ResponseEntity.ok(Map.of("message", "Webhook processed"));
    }
}
