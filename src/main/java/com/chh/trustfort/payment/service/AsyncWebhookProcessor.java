package com.chh.trustfort.payment.service;//package com.chh.trustfort.payment.service;
//
//import com.chh.trustfort.payment.dto.PaystackWebhookPayload;
//import com.chh.trustfort.payment.enums.TransactionStatus;
//import com.chh.trustfort.payment.enums.TransactionType;
//import com.chh.trustfort.payment.model.LedgerEntry;
//import com.chh.trustfort.payment.model.Wallet;
//import com.chh.trustfort.payment.repository.LedgerEntryRepository;
//import com.chh.trustfort.payment.repository.UsersRepository;
//import com.chh.trustfort.payment.repository.WalletRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class AsyncWebhookProcessor {
//
//    private final WalletRepository walletRepository;
//    private final UsersRepository usersRepository;
//    private final LedgerEntryRepository ledgerEntryRepository;
//
//    @Async
//    public void processPaystackWebhook(PaystackWebhookPayload payload) {
//        try {
//            if (!"charge.success".equals(payload.getEvent())) {
//                log.info("⚠️ Ignored non-successful Paystack event: {}", payload.getEvent());
//                return;
//            }
//
//            String reference = payload.getData().getReference();
//            boolean alreadyProcessed = ledgerEntryRepository.existsByReference(reference);
//            if (alreadyProcessed) {
//                log.warn("⚠️ Duplicate webhook received for reference: {}", reference);
//                return;
//            }
//
//            String email = payload.getData().getCustomer().getEmail();
//            log.info("💰 Processing Paystack webhook for email: {}", email);
//
//            String walletId = payload.getData().getMetadata().getWalletId();
//            BigDecimal amount = payload.getData().getAmount().divide(new BigDecimal("100"));
//
//            Wallet wallet = walletRepository.findByWalletId(walletId)
//                    .orElseThrow(() -> new RuntimeException("Wallet not found"));
//
//            wallet.setBalance(wallet.getBalance().add(amount));
//            walletRepository.updateUser(wallet);
//
//            LedgerEntry entry = new LedgerEntry();
//            entry.setWalletId(wallet.getWalletId());
//            entry.setTransactionType(TransactionType.CREDIT);
//            entry.setAmount(amount);
//            entry.setStatus(TransactionStatus.COMPLETED);
//            entry.setDescription("Paystack card payment funding");
//            entry.setReference(reference);
//            ledgerEntryRepository.save(entry);
//
//            log.info("✅ Wallet {} credited with {} via Paystack", walletId, amount);
//        } catch (Exception e) {
//            log.error("❌ Async webhook processing failed: {}", e.getMessage(), e);
//        }
//    }
//}
