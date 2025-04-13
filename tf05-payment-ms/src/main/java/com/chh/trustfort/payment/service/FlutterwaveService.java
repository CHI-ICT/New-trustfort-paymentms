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

    @Value("${flutterwave.secret.key}")
    private String flutterwaveSecretKey;

    private final OkHttpClient httpClient = new OkHttpClient();

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

                log.info("🔍 Flutterwave full response: {}", root.toPrettyString());

                JsonNode dataNode = root.path("data");

                if (dataNode.isMissingNode()) {
                    throw new RuntimeException("Flutterwave response missing 'data' field");
                }

                String status = dataNode.path("status").asText(null);
                String emailAddress = dataNode.path("customer").path("email").asText(null);
                String amountString = dataNode.path("amount").asText(null);

                if (status == null || !"successful".equalsIgnoreCase(status)) {
                    throw new RuntimeException("Payment not successful or missing status field: " + status);
                }

                if (emailAddress == null) {
                    throw new RuntimeException("Missing email in Flutterwave response");
                }

                if (amountString == null) {
                    throw new RuntimeException("Missing amount in Flutterwave response");
                }

                BigDecimal amount = new BigDecimal(amountString);

                // 🧠 Get user's wallet
                Optional<Wallet> optionalWallet = walletRepository.findByEmailAddress(emailAddress);
                if (optionalWallet.isEmpty()) {
                    throw new RuntimeException("No wallet found for email: " + emailAddress);
                }

                Wallet wallet = optionalWallet.get();

                // 💰 Credit wallet
                wallet.setBalance(wallet.getBalance().add(amount));
                walletRepository.updateUser(wallet);

                // 🧾 Ledger
                LedgerEntry ledger = new LedgerEntry();
                ledger.setWalletId(wallet.getWalletId());
                ledger.setTransactionType(TransactionType.CREDIT);
                ledger.setAmount(amount);
                ledger.setStatus(TransactionStatus.COMPLETED);
                ledger.setDescription("Flutterwave payment verification");
                ledger.setReference(request.getReference());
                ledgerEntryRepository.save(ledger);

                VerifyFlutterwaveResponse res = new VerifyFlutterwaveResponse();
                res.setStatus("success");
                res.setMessage("Wallet funded via Flutterwave");
                res.setCreditedAmount(amount);
                res.setWalletId(wallet.getWalletId());

                return res;
            }

        } catch (Exception e) {
            log.error("❌ Flutterwave verification error", e);
            throw new RuntimeException("Could not verify payment: " + e.getMessage());
        }
    }


    public String initiatePayment(InitiatePaymentRequest request) {
        try {
            // 🔐 Step 1: Create tx_ref (your own transaction reference)
            String txRef = "FLW-" + System.currentTimeMillis(); // generate once

            int amountInKobo = request.getAmount().multiply(new BigDecimal("100")).intValue();

            Map<String, Object> payload = Map.of(
                    "tx_ref", txRef,
                    "amount", amountInKobo / 100,
                    "currency", "NGN",
                    "redirect_url", "https://google.com", // change later
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

                if (jsonResponse.path("status").asText().equalsIgnoreCase("success")) {
                    String authorizationUrl = jsonResponse.path("data").path("link").asText();

                    // ✅ Just log your generated tx_ref
                    log.info("✅ Flutterwave Payment Initiated");
                    log.info("🔗 Authorization URL: {}", authorizationUrl);
                    log.info("🧾 tx_ref (Client reference): {}", txRef);

                    return authorizationUrl;
                } else {
                    throw new RuntimeException("Flutterwave payment initiation failed");
                }
            }

        } catch (Exception e) {
            log.error("Error initiating Flutterwave payment", e);
            throw new RuntimeException("Flutterwave initiation error");
        }
    }

}
