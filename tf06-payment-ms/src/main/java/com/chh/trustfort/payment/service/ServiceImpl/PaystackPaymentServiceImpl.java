package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.dto.JournalEntryRequest;
import com.chh.trustfort.payment.enums.ReferenceStatus;
import com.chh.trustfort.payment.model.*;
import com.chh.trustfort.payment.payload.FundWalletRequestPayload;
import com.chh.trustfort.payment.repository.PaymentFailureLogRepository;
import com.chh.trustfort.payment.repository.PaymentReferenceRepository;
import com.chh.trustfort.payment.repository.WalletRepository;
import com.chh.trustfort.payment.repository.WebhookLogRepository;
import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.AccountingClient;
import com.chh.trustfort.payment.service.WalletService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaystackPaymentServiceImpl implements PaystackPaymentService {

    @Value("${paystack.secret-key}")
    private String PAYSTACK_SECRET_KEY;

    @Value("${paystack.initiate-url}")
    private String PAYSTACK_INITIATE_URL;

    @Value("${paystack.verify-url}")
    private String PAYSTACK_VERIFY_URL;

    private final RestTemplate restTemplate;
    private final Gson gson;
    private final AesService aesService;
    private final WalletRepository walletRepository;
    private final PaymentReferenceRepository paymentReferenceRepository;
    private WalletService walletService;

    @Autowired
    public void setWalletService(@Lazy WalletService walletService) {
        this.walletService = walletService;
    }
    private final WebhookLogRepository webhookLogRepository;
    private final PaymentFailureLogRepository failureLogRepository;
    private final AccountingClient accountingClient;

    public String initiatePaystackPayment(FundWalletRequestPayload request, AppUser appUser) {
        // 🔁 Generate transaction reference
        String txRef = "PAYSTACK-" + System.currentTimeMillis();
        log.info("🔁 Generated Paystack Transaction Reference: {}", txRef);

        // 🔍 Get wallet and user
        Wallet wallet = walletRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        Users user = wallet.getUsers();
        if (user == null) throw new RuntimeException("Wallet is not linked to a user");

        String phone = wallet.getUserId();
        String email = appUser.getEmail() != null ? appUser.getEmail() : phone + "@chi.com";

        // 🔧 Build payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("amount", request.getAmount().multiply(BigDecimal.valueOf(100)).intValue()); // in kobo
        payload.put("reference", txRef);
        payload.put("callback_url", "https://webhook.site/02155d03-f601-4321-a978-8b85daa0b043/your-paystack-redirect"); // update if needed
        payload.put("metadata", Map.of("userId", phone));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(PAYSTACK_SECRET_KEY);

        HttpEntity<String> entity = new HttpEntity<>(gson.toJson(payload), headers);
        log.info("📤 Sending Paystack payment init request: {}", gson.toJson(payload));

        ResponseEntity<String> response = restTemplate.postForEntity(PAYSTACK_INITIATE_URL, entity, String.class);
        log.info("📥 Paystack raw response: {}", response.getBody());

        JsonObject body = JsonParser.parseString(response.getBody()).getAsJsonObject();
        JsonObject data = body.getAsJsonObject("data");

        if (data == null || !data.has("authorization_url")) {
            throw new RuntimeException("No payment link returned from Paystack");
        }

        // 💾 Save reference
        PaymentReference reference = new PaymentReference();
        reference.setTxRef(txRef);
        reference.setReferenceCode(txRef);
        reference.setUser(user);
        reference.setAmount(request.getAmount());
        reference.setCurrency(request.getCurrency());
        reference.setStatus(ReferenceStatus.PENDING);
        reference.setGateway("PAYSTACK");

        paymentReferenceRepository.save(reference);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("status", "success");
        responseMap.put("paymentLink", data.get("authorization_url").getAsString());
        responseMap.put("reference", txRef);

        return aesService.encrypt(gson.toJson(responseMap), appUser);
    }

    public boolean verifyPaystackPayment(String txRef) {
        try {
            log.info("🔍 Verifying Paystack payment for txRef: {}", txRef);

            PaymentReference reference = paymentReferenceRepository.findByTxRef(txRef)
                    .orElseThrow(() -> new RuntimeException("Payment reference not found"));

            if (ReferenceStatus.VERIFIED.equals(reference.getStatus())) {
                log.warn("⚠️ Reference already verified: {}", txRef);
                return true;
            }

            Users user = reference.getUser();
            if (user == null) {
                log.error("❌ No user found for txRef: {}", txRef);
                return false;
            }

            String verifyUrl = PAYSTACK_VERIFY_URL + "/" + txRef;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(PAYSTACK_SECRET_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(verifyUrl, HttpMethod.GET, entity, String.class);
            JsonObject data = JsonParser.parseString(response.getBody())
                    .getAsJsonObject()
                    .getAsJsonObject("data");

            if (data == null) return false;

            String status = data.get("status").getAsString();
            BigDecimal paidAmount = data.get("amount").getAsBigDecimal().divide(BigDecimal.valueOf(100));
            String currency = data.get("currency").getAsString();

            if (!"success".equalsIgnoreCase(status)
                    || paidAmount.compareTo(reference.getAmount()) < 0
                    || !currency.equalsIgnoreCase(reference.getCurrency())) {
                failureLogRepository.save(PaymentFailureLog.builder()
                        .txRef(txRef)
                        .expectedAmount(reference.getAmount())
                        .receivedAmount(paidAmount)
                        .expectedCurrency(reference.getCurrency())
                        .receivedCurrency(currency)
                        .statusReturned(status)
                        .userPhone(reference.getUser().getPhoneNumber())
                        .gateway("PAYSTACK")
                        .reason("Mismatch in status/amount/currency")
                        .build());
                return false;
            }

            // ✅ Credit Wallet
            List<Wallet> wallets = Collections.singletonList(user.getWallet());
            if (wallets == null || wallets.isEmpty()) {
                log.error("❌ No wallet found for user in txRef: {}", txRef);
                return false;
            }

            Wallet wallet = wallets.get(0);
            boolean credited = walletService.creditWalletByPhone(wallet.getUserId(), paidAmount, txRef, "Paystack Funding");

            if (!credited) {
                log.error("❌ Wallet not credited for txRef: {}", txRef);
                return false;
            }

            // ✅ Journal Entry
            JournalEntryRequest journal = new JournalEntryRequest();
            journal.setAccountCode(wallet.getAccountCode() != null ? wallet.getAccountCode() : "WALLET-FUNDING");
            journal.setWalletId(wallet.getWalletId());
            journal.setTransactionType("CREDIT");
            journal.setAmount(paidAmount);
            journal.setReference(txRef);
            journal.setDescription("Paystack Wallet Funding");
            journal.setDepartment("WALLET");
            journal.setBusinessUnit("TRUSTFORT");
            journal.setTransactionDate(LocalDateTime.now());

            try {
                accountingClient.postJournalEntryInternal(journal);
            } catch (Exception ex) {
                log.error("❌ Failed to post journal for txRef {}: {}", txRef, ex.getMessage(), ex);
            }

            // ✅ Update reference
            reference.setStatus(ReferenceStatus.VERIFIED);
            reference.setVerifiedAt(LocalDateTime.now());
            paymentReferenceRepository.save(reference);

            return true;

        } catch (Exception e) {
            log.error("❌ Paystack verification failed for txRef {}: {}", txRef, e.getMessage(), e);
            return false;
        }
    }

    public boolean reverifyAndCredit(PaymentReference reference) {
        try {
            if (ReferenceStatus.VERIFIED.equals(reference.getStatus())) return true;
            return verifyPaystackPayment(reference.getTxRef());
        } catch (Exception e) {
            log.error("❌ Reverification failed for txRef {}: {}", reference.getTxRef(), e.getMessage(), e);
            return false;
        }
    }

    @Scheduled(fixedDelay = 600000) // every 10 mins
    public void reconcilePendingPaystackPayments() {
        List<PaymentReference> pending = paymentReferenceRepository.findByStatusAndGateway(ReferenceStatus.PENDING, "PAYSTACK");

        int attempted = 0, success = 0;
        for (PaymentReference ref : pending) {
            if (!webhookLogRepository.existsByTxRef(ref.getTxRef())) continue;
            attempted++;
            if (reverifyAndCredit(ref)) success++;
        }

        log.info("✅ Paystack Reconciliation Complete. Attempted: {}, Credited: {}", attempted, success);
    }
}
