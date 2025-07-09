package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.dto.JournalEntryRequest;
import com.chh.trustfort.payment.enums.ReferenceStatus;
import com.chh.trustfort.payment.model.PaymentReference;
import com.chh.trustfort.payment.model.Wallet;
import com.chh.trustfort.payment.model.WebhookLog;
import com.chh.trustfort.payment.repository.PaymentReferenceRepository;
import com.chh.trustfort.payment.repository.WebhookLogRepository;
import com.chh.trustfort.payment.service.AccountingClient;
import com.chh.trustfort.payment.service.PaystackWebhookService;
import com.chh.trustfort.payment.service.WalletService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaystackWebhookServiceImpl implements PaystackWebhookService {

    private final WebhookLogRepository webhookLogRepository;
    private final PaymentReferenceRepository paymentReferenceRepository;
    private final WalletService walletService;
    private final AccountingClient accountingClient;

    @Override
    @Transactional
    public boolean handleWebhook(String payload) {
        try {
            JsonObject json = JsonParser.parseString(payload).getAsJsonObject();
            JsonObject data = json.getAsJsonObject("data");

            String event = json.get("event").getAsString();
            String txRef = data.get("reference").getAsString();

            if (!"charge.success".equalsIgnoreCase(event)) {
                log.warn("🔁 Ignoring unsupported Paystack event: {}", event);
                return true; // Ignore gracefully
            }

            // ✅ Check if webhook already logged
            if (webhookLogRepository.existsByTxRef(txRef)) {
                log.warn("⛔ Duplicate webhook received for txRef: {}", txRef);
                return false;
            }

            // 💾 Log webhook
            WebhookLog logEntry = new WebhookLog();
            logEntry.setTxRef(txRef);
            logEntry.setRawPayload(payload);
            logEntry.setEventType(event);
            logEntry.setProcessed(false);
            webhookLogRepository.save(logEntry);

            // 🔍 Lookup payment reference
            PaymentReference reference = paymentReferenceRepository.findByTxRef(txRef)
                    .orElseThrow(() -> new RuntimeException("Payment reference not found for txRef: " + txRef));

            if (ReferenceStatus.VERIFIED.equals(reference.getStatus())) {
                log.warn("⚠️ Reference already verified via webhook: {}", txRef);
                return true;
            }

            BigDecimal paidAmount = data.get("amount").getAsBigDecimal().divide(BigDecimal.valueOf(100));
            String currency = data.get("currency").getAsString();
            String status = data.get("status").getAsString();

            if (!"success".equalsIgnoreCase(status)
                    || paidAmount.compareTo(reference.getAmount()) < 0
                    || !currency.equalsIgnoreCase(reference.getCurrency())) {
                log.warn("⚠️ Mismatch in webhook details for txRef: {}", txRef);
                return false;
            }

            // 💰 Credit wallet
            Wallet wallet = reference.getUser().getWallet();
            boolean credited = walletService.creditWalletByPhone(wallet.getUserId(), paidAmount, txRef, "Webhook - Paystack");

            if (!credited) {
                log.error("❌ Wallet credit failed for txRef: {}", txRef);
                return false;
            }

            // 📘 Post journal
            JournalEntryRequest journal = new JournalEntryRequest();
            journal.setAccountCode(wallet.getAccountCode() != null ? wallet.getAccountCode() : "WALLET-FUNDING");
            journal.setWalletId(wallet.getWalletId());
            journal.setTransactionType("CREDIT");
            journal.setAmount(paidAmount);
            journal.setReference(txRef);
            journal.setDescription("Paystack Wallet Funding via Webhook");
            journal.setDepartment("WALLET");
            journal.setBusinessUnit("TRUSTFORT");
            journal.setTransactionDate(LocalDateTime.now());

            try {
                accountingClient.postJournalEntryInternal(journal);
            } catch (Exception ex) {
                log.error("❌ Failed to post journal entry: {}", ex.getMessage(), ex);
            }

            // ✅ Mark reference as verified
            reference.setStatus(ReferenceStatus.VERIFIED);
            reference.setVerifiedAt(LocalDateTime.now());
            paymentReferenceRepository.save(reference);

            // ✅ Mark webhook as processed
            logEntry.setProcessed(true);
            webhookLogRepository.save(logEntry);

            return true;

        } catch (Exception ex) {
            log.error("❌ Exception processing Paystack webhook: {}", ex.getMessage(), ex);
            return false;
        }
    }
}
