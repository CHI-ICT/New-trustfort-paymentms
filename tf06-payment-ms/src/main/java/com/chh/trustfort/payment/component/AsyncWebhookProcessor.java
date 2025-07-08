//package com.chh.trustfort.payment.component;
//
//import com.chh.trustfort.payment.model.WebhookLog;
//import com.chh.trustfort.payment.payload.CreditWalletRequestPayload;
//import com.chh.trustfort.payment.repository.WebhookLogRepository;
//import com.chh.trustfort.payment.service.WalletService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.Map;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class AsyncWebhookProcessor {
//
//    private final @Lazy WalletService walletService;
//    private final WebhookLogRepository webhookLogRepository;
//
//    @Async
//    public void processPaystackWebhook(Map<String, Object> payload) {
//        try {
//            String event = (String) payload.get("event");
//
//            if (!"charge.success".equalsIgnoreCase(event) && !"transfer.success".equalsIgnoreCase(event)) {
//                log.info("ℹ️ Ignoring non-payment event: {}", event);
//                return;
//            }
//
//            Map<String, Object> data = (Map<String, Object>) payload.get("data");
//
//            String reference = (String) data.get("reference");
//            Integer amountInKobo = (Integer) data.get("amount");
//            String email = ((Map<String, String>) data.get("customer")).get("email");
//
//            // ✅ Check for duplicate webhook (idempotency)
//            if (webhookLogRepository.findByReference(reference).isPresent()) {
//                log.warn("🔁 Duplicate webhook received for reference: {}. Skipping.", reference);
//                return;
//            }
//
//            BigDecimal amount = new BigDecimal(amountInKobo).divide(new BigDecimal("100"));
//
//            // ✅ Process wallet credit
//            walletService.creditWalletByEmail(email, amount, reference);
//            log.info("✅ Wallet credited for {} with ₦{}", email, amount);
//
//            // ✅ Save webhook log
//            WebhookLog logEntry = new WebhookLog();
//            logEntry.setTxRef(reference);
//            logEntry.setEventType(event);
//            webhookLogRepository.save(logEntry);
//
//        } catch (Exception e) {
//            log.error("❌ Exception while processing Paystack webhook async", e);
//        }
//    }
//}
