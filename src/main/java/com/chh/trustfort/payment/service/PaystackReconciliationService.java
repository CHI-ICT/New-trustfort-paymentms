package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.enums.TransactionStatus;
import com.chh.trustfort.payment.enums.TransactionType;
import com.chh.trustfort.payment.model.LedgerEntry;
import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.model.Wallet;
import com.chh.trustfort.payment.repository.LedgerEntryRepository;
import com.chh.trustfort.payment.repository.UsersRepository;
import com.chh.trustfort.payment.repository.WalletRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaystackReconciliationService {

    @Value("${paystack.secret.key}")
    private String paystackSecret;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final UsersRepository usersRepository;

    @Scheduled(fixedRate = 60 * 60 * 1000) // Every hour
    public void reconcilePaystackDeposits() {
        log.info("🔁 Starting Paystack deposit reconciliation...");

        Request request = new Request.Builder()
                .url("https://api.paystack.co/transaction?status=success")
                .get()
                .addHeader("Authorization", "Bearer " + paystackSecret)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            log.info("Response from Paystack: {}", responseBody);
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            if (!jsonNode.get("status").asBoolean()) {
                log.error("❌ Failed to fetch Paystack transactions: {}", jsonNode);
                return;
            }

            for (JsonNode transaction : jsonNode.get("data")) {
                String reference = transaction.get("reference").asText();
                String email = transaction.get("customer").get("email").asText();
                BigDecimal amount = new BigDecimal(transaction.get("amount").asInt()).divide(BigDecimal.valueOf(100));

                boolean alreadyProcessed = ledgerEntryRepository.existsByDescription("Webhook credit from Paystack: " + reference);
                log.info("Transaction processed: {}", alreadyProcessed);
                if (alreadyProcessed) continue;

                Users user = usersRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found: " + email));
                log.info("User found: {}", user != null ? user.getEmail() : "Not Found");


                Wallet wallet = walletRepository.findByEmailAddress(email)
                        .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + email));
                log.info("Wallet found: {}", wallet != null ? wallet.getWalletId() : "Not Found");

                log.info("Wallet balance before update: {}", wallet.getBalance());
                wallet.setBalance(wallet.getBalance().add(amount));
                log.info("Wallet balance after update: {}", wallet.getBalance());

                walletRepository.updateUser(wallet);

                LedgerEntry entry = new LedgerEntry();
                entry.setWalletId(wallet.getWalletId());
                entry.setAmount(amount);
                entry.setTransactionType(TransactionType.CREDIT);
                entry.setStatus(TransactionStatus.COMPLETED);
                entry.setDescription("Webhook credit from Paystack: " + reference);
                ledgerEntryRepository.save(entry);
                log.info("Saved ledger entry: {}", entry.getDescription());

                log.info("✅ Reconciled missed deposit for {} - Amount: {}", email, amount);
            }

            log.info("✅ Paystack reconciliation job completed.");
        } catch (Exception e) {
            log.error("❌ Error during Paystack reconciliation", e);
        }
    }
}
