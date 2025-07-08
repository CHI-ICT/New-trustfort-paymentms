//package com.chh.trustfort.payment.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.*;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.Map;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class PaystackTransferService {
//
//    @Value("${paystack.secret.key}")
//    private String paystackSecret;
//
//    private final OkHttpClient httpClient = new OkHttpClient();
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    // ✅ Step 1: Create Paystack Recipient
//    public String createRecipient(String name, String accountNumber, String bankCode) {
//        try {
//            log.info("🔁 Creating Paystack recipient with details - name: {}, accountNumber: {}, bankCode: {}", name, accountNumber, bankCode);
//
//            MediaType mediaType = MediaType.parse("application/json");
//
//            Map<String, String> payloadMap = Map.of(
//                    "type", "nuban",
//                    "name", name,
//                    "account_number", accountNumber,
//                    "bank_code", bankCode,
//                    "currency", "NGN"
//            );
//
//            String payloadJson = objectMapper.writeValueAsString(payloadMap);
//            log.info("🚀 Sending payload to Paystack: {}", payloadJson);
//
//            RequestBody body = RequestBody.create(payloadJson, mediaType);
//
//            Request request = new Request.Builder()
//                    .url("https://api.paystack.co/transferrecipient")
//                    .post(body)
//                    .addHeader("Authorization", "Bearer " + paystackSecret)
//                    .addHeader("Content-Type", "application/json")
//                    .build();
//
//            try (Response response = httpClient.newCall(request).execute()) {
//                String responseBody = response.body().string();
//                Map<?, ?> jsonResponse = objectMapper.readValue(responseBody, Map.class);
//
//                log.info("📦 Paystack recipient response: {}", jsonResponse);
//
//                if (!Boolean.TRUE.equals(jsonResponse.get("status"))) {
//                    log.error("❌ Failed to create recipient: {}", jsonResponse);
//                    log.error("❌ Recipient creation failed. Paystack response body: {}", responseBody);
//                    throw new RuntimeException("Error creating transfer recipient");
//                }
//
//                Map<?, ?> data = (Map<?, ?>) jsonResponse.get("data");
//                return data.get("recipient_code").toString();
//            }
//
//        } catch (Exception e) {
//            log.error("❌ Exception while creating Paystack recipient", e);
//            throw new RuntimeException("Error creating transfer recipient");
//        }
//    }
//
//    // ✅ Step 2: Initiate Transfer
//    public String initiateTransfer(BigDecimal amount, String recipientCode, String reason) {
//        try {
//            int amountInKobo = amount.multiply(new BigDecimal("100")).intValue();
//
//            Map<String, Object> transferPayload = Map.of(
//                    "source", "balance",
//                    "amount", amountInKobo,
//                    "recipient", recipientCode,
//                    "reason", reason
//            );
//
//            String payloadJson = objectMapper.writeValueAsString(transferPayload);
//            log.info("🚀 Sending transfer payload to Paystack: {}", payloadJson);
//
//            RequestBody body = RequestBody.create(payloadJson, MediaType.parse("application/json"));
//
//            Request request = new Request.Builder()
//                    .url("https://api.paystack.co/transfer")
//                    .post(body)
//                    .addHeader("Authorization", "Bearer " + paystackSecret)
//                    .addHeader("Content-Type", "application/json")
//                    .build();
//
//            try (Response response = httpClient.newCall(request).execute()) {
//                String responseBody = response.body().string();
//                Map<?, ?> json = objectMapper.readValue(responseBody, Map.class);
//
//                log.info("📦 Paystack transfer response: {}", json);
//
//                if (Boolean.TRUE.equals(json.get("status"))) {
//                    Map<?, ?> data = (Map<?, ?>) json.get("data");
//                    return data.get("transfer_code").toString();
//                } else {
//                    log.error("❌ Transfer failed: {}", json);
//                    throw new RuntimeException("Transfer initiation failed");
//                }
//            }
//
//        } catch (Exception e) {
//            log.error("❌ Initiate transfer error", e);
//            throw new RuntimeException("Error initiating transfer");
//        }
//    }
//}
