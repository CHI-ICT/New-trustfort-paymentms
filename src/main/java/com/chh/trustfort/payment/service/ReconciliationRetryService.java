package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.enums.TransactionStatus;
import com.chh.trustfort.payment.model.LedgerEntry;
import com.chh.trustfort.payment.model.SettlementAccount;
import com.chh.trustfort.payment.repository.LedgerEntryRepository;
import com.chh.trustfort.payment.repository.SettlementAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReconciliationRetryService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final SettlementAccountRepository settlementAccountRepository;
    private final FCMBIntegrationService fcmbIntegrationService;
    private final WalletService walletService;

    // 🔁 Runs every 30 minutes
    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void retryFailedWithdrawals() {
        log.info("🔁 Starting reconciliation retry job...");
        List<LedgerEntry> failedOrPendingEntries = ledgerEntryRepository.findByStatusIn(List.of(
                TransactionStatus.FAILED,
                TransactionStatus.PENDING
        ));

        for (LedgerEntry entry : failedOrPendingEntries) {
            try {
                log.info("🔄 Retrying ledger entry ID: {} for wallet ID: {}", entry.getId(), entry.getWalletId());

                String settlementAccountNumber = "MOCK-FCMB-001";
                BigDecimal amount = entry.getAmount();

                boolean transferSuccess = fcmbIntegrationService.transferFunds(settlementAccountNumber, amount);

                if (transferSuccess) {
                    entry.setStatus(TransactionStatus.COMPLETED);
                    log.info("✅ Transfer success. Entry marked COMPLETED: {}", entry.getId());
                } else {
                    entry.setStatus(TransactionStatus.FAILED);
                    walletService.updateWalletBalance(entry.getWalletId(), amount.doubleValue());
                    log.warn("❌ Retry failed. Entry remains FAILED and wallet refunded: {}", entry.getId());
                }
                ledgerEntryRepository.save(entry);

                // Ensure settlement balance remains accurate
                SettlementAccount sa = settlementAccountRepository.findByAccountNumber(settlementAccountNumber);
                if (sa != null && transferSuccess) {
                    sa.setBalance(sa.getBalance().add(amount));
                    settlementAccountRepository.save(sa);
                }

            } catch (Exception e) {
                log.error("❌ Error retrying ledger entry ID: {}", entry.getId(), e);
            }
        }

        log.info("✅ Reconciliation retry job completed.");
    }
} 
