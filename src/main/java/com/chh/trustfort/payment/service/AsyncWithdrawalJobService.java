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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncWithdrawalJobService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final SettlementAccountRepository settlementAccountRepository;
    private final MockFCMBIntegrationService mockFCMBIntegrationService;

    private static final String FCMB_SETTLEMENT_ACCOUNT = "FCMB-SETTLEMENT-001";

    @Scheduled(fixedDelay = 300000) // every 5 minutes
    @Transactional
    public void processPendingWithdrawals() {
        log.info("🔁 Checking for pending withdrawals...");

        List<LedgerEntry> pendingWithdrawals = ledgerEntryRepository.findByStatus(TransactionStatus.PENDING);

        for (LedgerEntry entry : pendingWithdrawals) {
            try {
                log.info("🔄 Processing withdrawal for wallet: {} | Amount: {}", entry.getWalletId(), entry.getAmount());

                // Ensure settlement account exists
                SettlementAccount settlementAccount = settlementAccountRepository.findByAccountNumber(FCMB_SETTLEMENT_ACCOUNT);
                if (settlementAccount == null) {
                    settlementAccount = new SettlementAccount();
                    settlementAccount.setAccountNumber(FCMB_SETTLEMENT_ACCOUNT);
                    settlementAccount.setBalance(entry.getAmount());
                } else {
                    settlementAccount.setBalance(settlementAccount.getBalance().add(entry.getAmount()));
                }
                settlementAccountRepository.save(settlementAccount);

                boolean fcmbSuccess = mockFCMBIntegrationService.transferFunds(FCMB_SETTLEMENT_ACCOUNT, entry.getAmount());
                if (fcmbSuccess) {
                    entry.setStatus(TransactionStatus.COMPLETED);
                    log.info("✅ FCMB transfer successful. Ledger marked COMPLETED for wallet: {}", entry.getWalletId());
                } else {
                    entry.setStatus(TransactionStatus.FAILED);
                    log.warn("❌ FCMB transfer failed. Ledger marked FAILED for wallet: {}", entry.getWalletId());
                }

                ledgerEntryRepository.save(entry);

            } catch (Exception e) {
                log.error("❌ Error during withdrawal processing for wallet: {} | Reason: {}", entry.getWalletId(), e.getMessage());
            }
        }

        log.info("✅ Pending withdrawal processing completed.");
    }
}
