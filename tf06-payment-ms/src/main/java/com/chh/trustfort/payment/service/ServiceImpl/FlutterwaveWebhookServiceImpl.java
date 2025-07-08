package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.model.WebhookLog;
import com.chh.trustfort.payment.repository.LedgerEntryRepository;
import com.chh.trustfort.payment.repository.WalletRepository;
import com.chh.trustfort.payment.repository.WebhookLogRepository;
import com.chh.trustfort.payment.service.FlutterwaveWebhookService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlutterwaveWebhookServiceImpl implements FlutterwaveWebhookService {

    private final WalletRepository walletRepository;
    private final WebhookLogRepository webhookLogRepository;
    private final LedgerEntryRepository ledgerRepository;
    private final RestTemplate restTemplate = new RestTemplate();


//    @Override
//    public boolean handleWebhook(String payload) {
//        try {
//            JsonObject json = JsonParser.parseString(payload).getAsJsonObject();
//            String event = json.get("event").getAsString();
//
//            if (!"charge.success".equalsIgnoreCase(event)) {
//                log.warn("❌ Unsupported event type: {}", event);
//                return false;
//            }
//
//            JsonObject data = json.getAsJsonObject("data");
//            String txRef = data.get("tx_ref").getAsString();
//            String amount = data.get("amount").getAsString();
//
//            // ✅ Extract userId (phoneNumber) from meta
//            JsonArray metaArray = data.getAsJsonArray("meta");
//            String userId = null;
//            for (JsonElement element : metaArray) {
//                JsonObject metaItem = element.getAsJsonObject();
//                if ("userId".equalsIgnoreCase(metaItem.get("metaname").getAsString())) {
//                    userId = metaItem.get("metavalue").getAsString();
//                    break;
//                }
//            }
//
//            if (userId == null) {
//                log.error("❌ userId not found in webhook metadata");
//                return false;
//            }
//
//            // ✅ Check for duplicate tx_ref
//            if (ledgerRepository.findByTransactionReference(txRef).isPresent()) {
//                log.warn("⚠️ Duplicate transaction tx_ref: {}", txRef);
//                return false;
//            }
//
//            // ✅ Lookup wallet by userId (phoneNumber)
//            Wallet wallet = walletRepository.findByUserId(userId).stream().findFirst().orElse(null);
//            if (wallet == null) {
//                log.error("⚠️ No wallet found for userId: {}", userId);
//                return false;
//            }
//
//            BigDecimal creditAmount = new BigDecimal(amount);
//            wallet.setBalance(wallet.getBalance().add(creditAmount));
//            wallet.setLedgerBalance(wallet.getLedgerBalance().add(creditAmount));
//            walletRepository.updateUser(wallet);
//
//            // ✅ Save ledger transaction
//            LedgerEntry ledger = new LedgerEntry();
//            ledger.setWallet(wallet);
//            ledger.setWalletId(wallet.getWalletId()); // explicitly set walletId column
//            ledger.setTransactionReference(txRef);
//            ledger.setAmount(creditAmount);
//            ledger.setTransactionType(TransactionType.CREDIT);
//            ledger.setStatus(TransactionStatus.COMPLETED);
//            ledger.setNarration("Flutterwave wallet funding");
//
//            ledgerRepository.save(ledger);
//
//            // 🔄 Sync journal entry to accounting-ms
//            JournalEntryRequest journal = new JournalEntryRequest();
//            journal.setAccountCode("1001"); // Replace with actual COA code for wallet
//            journal.setWalletId(wallet.getWalletId());
//            journal.setTransactionType("CREDIT");
//            journal.setAmount(creditAmount);
//            journal.setDescription("Flutterwave wallet funding");
//            journal.setTransactionDate(LocalDate.now()); // or extract from webhook if available
//            journal.setReference(txRef);
//            journal.setDepartment("WALLET"); // or use user metadata
//            journal.setBusinessUnit("PAYMENT"); // or use user metadata
//
//            try {
//                String accountingUrl = "https://localhost:8445/trustfort/api/v1/accountingService/api/journal-entry";
//
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(MediaType.APPLICATION_JSON);
//
//                HttpEntity<JournalEntryRequest> entity = new HttpEntity<>(journal, headers);
//
//                restTemplate.postForEntity(accountingUrl, entity, Void.class);
//
//                log.info("✅ Journal entry successfully synced to accounting-ms");
//            } catch (Exception ex) {
//                log.error("❌ Failed to sync journal entry to accounting-ms: {}", ex.getMessage(), ex);
//            }
//
//
//            log.info("✅ Wallet funded successfully for user {} with amount {}", userId, creditAmount);
//            return true;
//
//        } catch (Exception e) {
//            log.error("❌ Error processing webhook: ", e);
//            return false;
//        }
//
//    }

//    @Override
//    @Transactional
//    public boolean handleWebhook(String payload) {
//        try {
//            JsonObject json = JsonParser.parseString(payload).getAsJsonObject();
//            String event = json.get("event").getAsString();
//
//            JsonObject data = json.getAsJsonObject("data");
//            String txRef = data.get("tx_ref").getAsString();
//
//            // ✅ Log and skip if already recorded
//            if (webhookLogRepository.existsByTxRef(txRef)) {
//                log.warn("⚠️ Webhook already received for tx_ref: {}", txRef);
//                return false;
//            }
//
//            WebhookLog logEntry = new WebhookLog();
//            logEntry.setTxRef(txRef);
//            logEntry.setEventType(event);
//            logEntry.setRawPayload(payload);
//            logEntry.setProcessed(false);
//            logEntry.setSourceIp("Webhook Source"); // You can pass IP from controller
//
//            webhookLogRepository.save(logEntry);
//
//            log.info("📩 Webhook logged successfully for tx_ref: {}", txRef);
//            return true;
//
//        } catch (Exception e) {
//            log.error("❌ Error logging webhook: ", e);
//            return false;
//        }
//    }
//
//}

    @Override
    @Transactional
    public boolean handleWebhook(String payload) {
        try {
            JsonObject json = JsonParser.parseString(payload).getAsJsonObject();
            String event = json.get("event").getAsString();
            JsonObject data = json.getAsJsonObject("data");
            String txRef = data.get("tx_ref").getAsString();

            // ✅ Avoid duplicate processing
            if (webhookLogRepository.existsByTxRef(txRef)) {
                log.warn("⚠️ Webhook already received for txRef: {}", txRef);
                return false;
            }

            // ✅ Log webhook for traceability
            WebhookLog logEntry = new WebhookLog();
            logEntry.setTxRef(txRef);
            logEntry.setEventType(event);
            logEntry.setRawPayload(payload);
            logEntry.setProcessed(false);
            logEntry.setSourceIp("Webhook Source");

            webhookLogRepository.save(logEntry);
            log.info("📩 Webhook logged successfully for txRef: {}", txRef);

            return true;

        } catch (Exception e) {
            log.error("❌ Error logging webhook for Flutterwave: ", e);
            return false;
        }
    }

}

