package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.dto.JournalEntryRequest;
import com.chh.trustfort.payment.model.Wallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class JournalPostingService {
    private final AccountingClient accountingClient;

    public String getSystemAccountCode(String purpose) {
        // This could be from DB, config, or switch-case
        switch (purpose.toUpperCase()) {
            case "PAYSTACK_CONTROL": return "2001201"; // Cash in Zenith
            case "FLUTTERWAVE_CONTROL": return "2001205"; // Cash in Access
            case "DEFAULT": return "2001203"; // General bank
            default: throw new RuntimeException("No control account for: " + purpose);
        }
    }


    public void postDoubleEntry(BigDecimal amount, String txRef, Wallet wallet, String description) {
        // 🔁 Get wallet account code
        String creditAccountCode = wallet.getAccountCode() != null ? wallet.getAccountCode() : "2001106";

        // 🔁 Hardcoded debit (Flutterwave clearing) — optionally get from Chart of Accounts
        String debitAccountCode = getSystemAccountCode("DEFAULT");

        // 🧾 DEBIT: Flutterwave clearing account
        JournalEntryRequest debitEntry = new JournalEntryRequest();
        debitEntry.setAccountCode(debitAccountCode);
        debitEntry.setTransactionType("DEBIT");
        debitEntry.setAmount(amount);
        debitEntry.setReference(txRef);
        debitEntry.setDescription("Incoming inflow");
        debitEntry.setWalletId(null); // system-level account
        debitEntry.setDepartment("WALLET");
        debitEntry.setBusinessUnit("TRUSTFORT");
        debitEntry.setTransactionDate(LocalDateTime.now());

        // 🧾 CREDIT: Wallet account
        JournalEntryRequest creditEntry = new JournalEntryRequest();
        creditEntry.setAccountCode(creditAccountCode);
        creditEntry.setTransactionType("CREDIT");
        creditEntry.setAmount(amount);
        creditEntry.setReference(txRef);
        creditEntry.setDescription(description);
        creditEntry.setWalletId(wallet.getWalletId());
        creditEntry.setDepartment("WALLET");
        creditEntry.setBusinessUnit("TRUSTFORT");
        creditEntry.setTransactionDate(LocalDateTime.now());

        try {
            // Post entries individually
            accountingClient.postJournalEntryInternal(debitEntry);
            accountingClient.postJournalEntryInternal(creditEntry);

            log.info("📘 Double-entry journal posted for txRef {}", txRef);
        } catch (Exception e) {
            log.error("❌ Failed to post double journal entries for txRef {}: {}", txRef, e.getMessage(), e);
            throw new RuntimeException("Journal posting failed");
        }
    }
}
