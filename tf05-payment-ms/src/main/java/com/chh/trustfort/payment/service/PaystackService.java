package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.Responses.ErrorResponse;
import com.chh.trustfort.payment.Responses.SuccessResponse;
import com.chh.trustfort.payment.constant.ResponseCode;
import com.chh.trustfort.payment.dto.InitiatePaymentRequest;
import com.chh.trustfort.payment.dto.PaystackWebhookPayload;
import com.chh.trustfort.payment.enums.TransactionStatus;
import com.chh.trustfort.payment.enums.TransactionType;
import com.chh.trustfort.payment.model.LedgerEntry;
import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.model.Wallet;
import com.chh.trustfort.payment.repository.UsersRepository;
import com.chh.trustfort.payment.repository.WalletRepository;
import com.chh.trustfort.payment.repository.LedgerEntryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaystackService {

    private final WalletRepository walletRepository;
    private final UsersRepository usersRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    @Value("${paystack.secret.key}")
    private String paystackSecret;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // STEP 1: Initiate Payment
    public String initiatePayment(InitiatePaymentRequest request) {
        try {
            // Convert amount to Kobo (multiply by 100)
            int amountInKobo = request.getAmount().multiply(new BigDecimal("100")).intValue();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(
                    objectMapper.writeValueAsString(Map.of(
                            "email", request.getEmail(),
                            "amount", amountInKobo,
                            "metadata", Map.of("walletId", request.getWalletId())
                    )),
                    mediaType
            );

            Request httpRequest = new Request.Builder()
                    .url("https://api.paystack.co/transaction/initialize")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + paystackSecret)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                String responseBody = response.body().string();
                Map<?, ?> jsonResponse = objectMapper.readValue(responseBody, Map.class);

                if ((Boolean) jsonResponse.get("status")) {
                    Map<?, ?> data = (Map<?, ?>) jsonResponse.get("data");
                    return data.get("authorization_url").toString();
                } else {
                    throw new RuntimeException("Payment initialization failed");
                }
            }
        } catch (Exception e) {
            log.error("Error initiating payment", e);
            throw new RuntimeException("Error initiating payment");
        }
    }

    // STEP 2: Handle Webhook
    public ResponseEntity<?> handleWebhook(PaystackWebhookPayload payload, String signature, HttpServletRequest request) {
        try {
//             Optional: Signature verification
//             String computedSignature = computeHMACSignature(request, paystackSecret);
//             if (!signature.equals(computedSignature)) {
//                 log.warn("⚠️ Invalid webhook signature from Paystack!");
//                 return ResponseEntity.status(403).body("Invalid signature");
//             }

            // Step 2: Process successful payment
            if ("charge.success".equals(payload.getEvent())) {
                String reference = payload.getData().getReference();

                boolean alreadyProcessed = ledgerEntryRepository.existsByReference(reference);
                if (alreadyProcessed) {
                    log.warn("⚠️ Duplicate webhook received for reference: {}", reference);
                    return ResponseEntity.ok("Duplicate webhook - already processed");
                }
                String email = payload.getData().getCustomer().getEmail();
                log.info("💰 Payment from {}", email);

                String walletId = payload.getData().getMetadata().getWalletId();
                BigDecimal amount = payload.getData().getAmount().divide(new BigDecimal("100"));

                Wallet wallet = walletRepository.findByWalletId(walletId)
                        .orElseThrow(() -> new RuntimeException("Wallet not found"));

                wallet.setBalance(wallet.getBalance().add(amount));
                walletRepository.updateUser(wallet);

                LedgerEntry entry = new LedgerEntry();
                entry.setWalletId(wallet.getWalletId());
                entry.setTransactionType(TransactionType.CREDIT);
                entry.setAmount(amount);
                entry.setStatus(TransactionStatus.COMPLETED);
                entry.setDescription("Paystack card payment funding");
                entry.setReference(reference); // ✅ Now this works
                ledgerEntryRepository.save(entry);

                SuccessResponse response = new SuccessResponse();
                response.setResponseCode(String.valueOf(ResponseCode.SUCCESS_CODE));
                response.setResponseMessage("Wallet funded successfully");
                response.setNewBalance(wallet.getBalance());
                return ResponseEntity.ok(response);
            }


            return ResponseEntity.ok("Ignored");
        } catch (Exception e) {
            log.error("Webhook error: ", e);
            return ResponseEntity.status(500).body(new ErrorResponse("Webhook error", "99"));
        }
    }


    private String computeHMACSignature(HttpServletRequest request, String secretKey) throws Exception {
        byte[] bodyBytes = request.getInputStream().readAllBytes();
        Mac hmac = Mac.getInstance("HmacSHA512");
        hmac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
        byte[] hash = hmac.doFinal(bodyBytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
