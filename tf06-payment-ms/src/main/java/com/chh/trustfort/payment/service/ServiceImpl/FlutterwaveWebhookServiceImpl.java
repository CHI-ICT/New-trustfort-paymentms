package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.enums.TransactionStatus;
import com.chh.trustfort.payment.enums.TransactionType;
import com.chh.trustfort.payment.model.LedgerEntry;
import com.chh.trustfort.payment.model.Wallet;
import com.chh.trustfort.payment.repository.LedgerEntryRepository;
import com.chh.trustfort.payment.repository.WalletRepository;
import com.chh.trustfort.payment.service.FlutterwaveWebhookService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlutterwaveWebhookServiceImpl implements FlutterwaveWebhookService {

    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerRepository;

    @Override
    public boolean handleWebhook(String payload) {
        try {
            JsonObject json = JsonParser.parseString(payload).getAsJsonObject();
            String event = json.get("event").getAsString();

            if (!"charge.success".equalsIgnoreCase(event)) {
                log.warn("❌ Unsupported event type: {}", event);
                return false;
            }

            JsonObject data = json.getAsJsonObject("data");
            String txRef = data.get("tx_ref").getAsString();
            String amount = data.get("amount").getAsString();

            // ✅ Extract userId (phoneNumber) from meta
            JsonArray metaArray = data.getAsJsonArray("meta");
            String userId = null;
            for (JsonElement element : metaArray) {
                JsonObject metaItem = element.getAsJsonObject();
                if ("userId".equalsIgnoreCase(metaItem.get("metaname").getAsString())) {
                    userId = metaItem.get("metavalue").getAsString();
                    break;
                }
            }

            if (userId == null) {
                log.error("❌ userId not found in webhook metadata");
                return false;
            }

            // ✅ Check for duplicate tx_ref
            if (ledgerRepository.findByTransactionReference(txRef).isPresent()) {
                log.warn("⚠️ Duplicate transaction tx_ref: {}", txRef);
                return false;
            }

            // ✅ Lookup wallet by userId (phoneNumber)
            Wallet wallet = walletRepository.findByUserId(userId).stream().findFirst().orElse(null);
            if (wallet == null) {
                log.error("⚠️ No wallet found for userId: {}", userId);
                return false;
            }

            BigDecimal creditAmount = new BigDecimal(amount);
            wallet.setBalance(wallet.getBalance().add(creditAmount));
            wallet.setLedgerBalance(wallet.getLedgerBalance().add(creditAmount));
            walletRepository.updateUser(wallet);

            // ✅ Save ledger transaction
            LedgerEntry ledger = new LedgerEntry();
            ledger.setWallet(wallet);
            ledger.setWalletId(wallet.getWalletId()); // explicitly set walletId column
            ledger.setTransactionReference(txRef);
            ledger.setAmount(creditAmount);
            ledger.setTransactionType(TransactionType.CREDIT);
            ledger.setStatus(TransactionStatus.COMPLETED);
            ledger.setNarration("Flutterwave wallet funding");

            ledgerRepository.save(ledger);

            log.info("✅ Wallet funded successfully for user {} with amount {}", userId, creditAmount);
            return true;

        } catch (Exception e) {
            log.error("❌ Error processing webhook: ", e);
            return false;
        }
    }
}

