package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.enums.TransactionStatus;
import com.chh.trustfort.payment.model.WalletLedgerEntry;
import com.chh.trustfort.payment.repository.LedgerEntryRepository;
import com.chh.trustfort.payment.repository.SettlementAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReconciliationJobService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final SettlementAccountRepository settlementAccountRepository;
    private final FCMBIntegrationService fcmbIntegrationService;

    /**
     * Scheduled job to retry failed FCMB transfers.
     * Runs every 5 minutes.
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000) // every 5 minutes
    public void retryFailedFcmbTransfers() {
        log.info("🔁 Starting reconciliation job for failed FCMB transfers...");

        // 1. Find failed withdrawal ledger entries
        List<WalletLedgerEntry> failedTransfers = ledgerEntryRepository.findByStatusAndDescription(
                TransactionStatus.FAILED, "Wallet Withdrawal - Pending Settlement");

        for (WalletLedgerEntry entry : failedTransfers) {
            log.info("🔄 Retrying transfer for wallet ID: {}, amount: {}", entry.getWalletId(), entry.getAmount());

            boolean success = fcmbIntegrationService.transferFunds("FCMB-SETTLEMENT-001", entry.getAmount());

            if (success) {
                entry.setStatus(TransactionStatus.COMPLETED);
                ledgerEntryRepository.save(entry);
                log.info("✅ FCMB transfer retried and marked as COMPLETED for wallet: {}", entry.getWalletId());
            } else {
                log.warn("❌ Retry failed again for wallet: {}", entry.getWalletId());
            }
        }

        log.info("🔁 Reconciliation job completed.");
    }
}
