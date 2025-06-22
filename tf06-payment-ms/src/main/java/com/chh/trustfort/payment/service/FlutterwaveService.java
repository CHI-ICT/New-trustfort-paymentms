package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.Responses.VerifyFlutterwaveResponse;
import com.chh.trustfort.payment.dto.InitiatePaymentRequest;
import com.chh.trustfort.payment.dto.VerifyFlutterwaveRequest;
import com.chh.trustfort.payment.model.LedgerEntry;
import com.chh.trustfort.payment.model.Wallet;
import com.chh.trustfort.payment.repository.LedgerEntryRepository;
import com.chh.trustfort.payment.repository.WalletRepository;
import com.chh.trustfort.payment.enums.TransactionStatus;
import com.chh.trustfort.payment.enums.TransactionType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlutterwaveService {

    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient httpClient = new OkHttpClient();

    @Value("${flutterwave.secret.key}")
    private String flutterwaveSecretKey;

    @Transactional
    public VerifyFlutterwaveResponse verifyTransaction(VerifyFlutterwaveRequest request) {
        try {
            String url = "https://api.flutterwave.com/v3/transactions/" + request.getReference() + "/verify";

            Request httpRequest = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Authorization", "Bearer " + flutterwaveSecretKey)
                    .build();

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                String body = response.body().string();
                JsonNode root = objectMapper.readTree(body);

                log.info("🔍 Flutterwave response: {}", root.toPrettyString());

                JsonNode dataNode = root.path("data");
                if (dataNode.isMissingNode()) {
                    throw new RuntimeException("Missing 'data' node in Flutterwave response");
                }

                String status = dataNode.path("status").asText(null);
                String emailAddress = dataNode.path("customer").path("email").asText(null);
                String amountString = dataNode.path("amount").asText(null);

                if (!"successful".equalsIgnoreCase(status)) {
                    throw new RuntimeException("Transaction not successful: " + status);
                }

                if (emailAddress == null || amountString == null) {
                    throw new RuntimeException("Missing email or amount in Flutterwave response");
                }

                BigDecimal amount = new BigDecimal(amountString);

                Optional<Wallet> optionalWallet = walletRepository.findByEmailAddress(emailAddress);
                if (optionalWallet.isEmpty()) {
                    throw new RuntimeException("No wallet found for email: " + emailAddress);
                }

                Wallet wallet = optionalWallet.get();
                wallet.setBalance(wallet.getBalance().add(amount));
                walletRepository.updateUser(wallet);

                LedgerEntry ledger = new LedgerEntry();
                ledger.setWalletId(wallet.getWalletId());
                ledger.setTransactionType(TransactionType.CREDIT);
                ledger.setAmount(amount);
                ledger.setStatus(TransactionStatus.COMPLETED);
                ledger.setDescription("Flutterwave payment verification");
                ledger.setReference(request.getReference());
                ledgerEntryRepository.save(ledger);

                return VerifyFlutterwaveResponse.builder()
                        .status("success")
                        .message("Wallet funded via Flutterwave")
                        .walletId(wallet.getWalletId())
                        .creditedAmount(amount)
                        .build();
            }

        } catch (Exception e) {
            log.error("❌ Error during Flutterwave verification", e);
            throw new RuntimeException("Flutterwave verification failed: " + e.getMessage());
        }
    }

    public String initiatePayment(InitiatePaymentRequest request) {
        try {
            String txRef = "FLW-" + System.currentTimeMillis(); // Unique transaction ref
            int amountInKobo = request.getAmount().multiply(new BigDecimal("100")).intValue();

            Map<String, Object> payload = Map.of(
                    "tx_ref", txRef,
                    "amount", amountInKobo / 100,
                    "currency", "NGN",
                    "redirect_url", "https://google.com",
                    "customer", Map.of(
                            "email", request.getEmail(),
                            "name", "Trustfort Wallet User"
                    ),
                    "meta", Map.of("walletId", request.getWalletId()),
                    "customizations", Map.of(
                            "title", "Wallet Funding",
                            "description", "Funding your Trustfort wallet"
                    )
            );

            RequestBody body = RequestBody.create(
                    objectMapper.writeValueAsString(payload),
                    MediaType.parse("application/json")
            );

            Request httpRequest = new Request.Builder()
                    .url("https://api.flutterwave.com/v3/payments")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + flutterwaveSecretKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                String responseBody = response.body().string();
                JsonNode jsonResponse = objectMapper.readTree(responseBody);

                if ("success".equalsIgnoreCase(jsonResponse.path("status").asText())) {
                    String link = jsonResponse.path("data").path("link").asText();
                    log.info("✅ Payment initiated: tx_ref={}, link={}", txRef, link);
                    return link;
                } else {
                    throw new RuntimeException("Flutterwave payment initiation failed");
                }
            }

        } catch (Exception e) {
            log.error("❌ Flutterwave initiation error", e);
            throw new RuntimeException("Flutterwave payment initiation error: " + e.getMessage());
        }
    }
}
