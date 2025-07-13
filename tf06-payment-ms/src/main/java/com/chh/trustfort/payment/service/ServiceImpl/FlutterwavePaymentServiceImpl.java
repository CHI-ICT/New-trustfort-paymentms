package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.dto.JournalEntryRequest;
import com.chh.trustfort.payment.dto.VerifyFlutterwaveRequest;
import com.chh.trustfort.payment.enums.ReferenceStatus;
import com.chh.trustfort.payment.enums.WalletStatus;
import com.chh.trustfort.payment.model.*;
import com.chh.trustfort.payment.payload.FundWalletRequestPayload;
import com.chh.trustfort.payment.payload.OmniResponsePayload;
import com.chh.trustfort.payment.repository.*;
import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.AccountingClient;
import com.chh.trustfort.payment.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlutterwavePaymentServiceImpl implements FlutterwavePaymentService  {

    @Value("${flutterwave.secret-key}")
    private String FLW_SECRET_KEY;

    @Value("${flutterwave.initiate-url}")
    private String FLW_PAYMENT_URL;

    private final RestTemplate restTemplate;
    private final Gson gson;
    private final AesService aesService;
    private WalletService walletService;
    @Autowired
    public void setWalletService(@Lazy WalletService walletService) {
        this.walletService = walletService;
    }
    private final PaymentReferenceRepository paymentReferenceRepository;
    private final PaymentFailureLogRepository failureLogRepository;
    private final WalletRepository walletRepository;
    private final WebhookLogRepository webhookLogRepository;
    private final com.chh.trustfort.payment.service.AccountingClient accountingClient;





    public String initiateFlutterwavePayment(FundWalletRequestPayload request, AppUser appUser) {
        // 🔁 Step 1: Generate FLW Transaction Reference
        String txRef = "FLW-" + System.currentTimeMillis();
        log.info("🔁 Generated Flutterwave Transaction Reference: {}", txRef);

        // 📦 Step 2: Fetch wallet and extract userId (which is the phone number)
//        Wallet wallet = walletRepository.findByWalletId(request.getWalletId())
//                .orElseThrow(() -> new RuntimeException("❌ Wallet not found for walletId: " + request.getWalletId()));

        Wallet wallet = walletRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("❌ Wallet not found for userId: " + request.getUserId()));

        // 🔐 Step 2.1: Wallet status check
        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            log.warn("❌ Wallet is not active. walletId={}, status={}", wallet.getWalletId(), wallet.getStatus());
            throw new RuntimeException("❌ Cannot initiate payment: Wallet is currently " + wallet.getStatus().name().toLowerCase());
        }

        String phone = request.getUserId();

//        String phone = wallet.getUserId(); // This is the actual phone number
        if (phone == null || phone.trim().isEmpty()) {
            throw new RuntimeException("❌ Cannot initiate payment: Phone number (userId) is missing in Wallet.");
        }

        // 📧 Step 3: Get fallback email and name
        String email = appUser.getEmail() != null ? appUser.getEmail() : phone + "@chi.com";
        String name = appUser.getUserName() != null ? appUser.getUserName() : phone;

        // 🧾 Step 4: Build Flutterwave payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("tx_ref", txRef);
        payload.put("amount", request.getAmount());
        payload.put("currency", request.getCurrency());
//        payload.put("redirect_url", "https://www.flutterwave.com");
        payload.put("redirect_url", "https://webhook.site/02155d03-f601-4321-a978-8b85daa0b043/trustfort/api/v1/flutterwave-redirect");
        payload.put("payment_options", "card");

        Map<String, String> customer = new HashMap<>();
        customer.put("email", email);
        customer.put("phonenumber", phone);
        customer.put("name", name);
        payload.put("customer", customer);

        payload.put("meta", List.of(Map.of("metaname", "userId", "metavalue", phone)));

        // 🌐 Step 5: Send request to Flutterwave
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(FLW_SECRET_KEY);

        HttpEntity<String> entity = new HttpEntity<>(gson.toJson(payload), headers);
        log.info("📤 Sending Flutterwave request: {}", gson.toJson(payload));

        ResponseEntity<String> response = restTemplate.postForEntity(FLW_PAYMENT_URL, entity, String.class);
        log.info("📥 Flutterwave raw response: {}", response.getBody());

        JsonObject responseBody = JsonParser.parseString(response.getBody()).getAsJsonObject();
        JsonObject data = responseBody.getAsJsonObject("data");

        if (data == null || !data.has("link")) {
            throw new RuntimeException("❌ Failed to retrieve payment link from Flutterwave response");
        }

        log.info("📊 Parsed 'data' from Flutterwave response: {}", data);

        String paymentLink = data.has("link") ? data.get("link").getAsString() : null;
        String flutterwaveTxId = data.has("id") ? data.get("id").getAsString() : null;

// If only paymentLink is available, proceed with that
        if (paymentLink == null) {
            throw new RuntimeException("❌ Flutterwave response missing payment link.");
        }

        log.info("✅ Payment Link: {}", paymentLink);
        log.info("🆔 Transaction ID (optional at this stage): {}", flutterwaveTxId); // This may be null and that's okay


        // 👤 Step 6: Fetch Users entity from Wallet
        Users user = wallet.getUsers();
        if (user == null) {
            throw new RuntimeException("❌ No associated user found for wallet.");
        }
        log.info("✅ User resolved for payment reference: {}", user.getEmail());

        // 💾 Step 7: Save payment reference
        PaymentReference reference = new PaymentReference();
        reference.setTxRef(txRef);
        reference.setFlutterwaveTxId(flutterwaveTxId);
        reference.setReferenceCode(txRef); // This line is missing!
        reference.setUser(user);
        reference.setAmount(request.getAmount());
        reference.setCurrency(request.getCurrency());
        reference.setStatus(ReferenceStatus.PENDING);
        reference.setGateway("FLW");

        paymentReferenceRepository.save(reference);
        log.info("✅ PaymentReference saved for txRef: {}", txRef);

        // 🔐 Step 8: Return encrypted response
        Map<String, String> responseMap = new HashMap<>();
        String status = responseBody.has("status") ? responseBody.get("status").getAsString() : "error";
        String message = responseBody.has("message") ? responseBody.get("message").getAsString() : "Unknown error occurred";
        responseMap.put("status", status);
        responseMap.put("message", message);

        responseMap.put("paymentLink", paymentLink);

        return aesService.encrypt(gson.toJson(responseMap), appUser);
    }

    public boolean verifyFlutterwavePayment(String txRef, String transactionId) {
    try {
        log.info("🔍 Verifying Flutterwave Redirect: txRef={}, transactionId={}", txRef, transactionId);

        // 🔁 Step 1: Fetch payment reference by tx_ref
        PaymentReference reference = paymentReferenceRepository
                .findByTxRef(txRef)
                .orElseThrow(() -> new RuntimeException("❌ No payment reference found for txRef: " + txRef));

        // If already verified, return true immediately
        if (ReferenceStatus.VERIFIED.equals(reference.getStatus())) {
            log.warn("⚠️ Payment already verified for txRef: {}", txRef);
            return true;
        }

        // 🔁 Step 2: Call Flutterwave verify endpoint
        String url = "https://api.flutterwave.com/v3/transactions/" + transactionId + "/verify";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(FLW_SECRET_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        JsonObject data = JsonParser.parseString(response.getBody())
                .getAsJsonObject()
                .getAsJsonObject("data");

        if (data == null) return false;

        // 🔐 Validate response
        String responseTxRef = data.get("tx_ref").getAsString();
        String status = data.get("status").getAsString();
        BigDecimal paidAmount = data.get("amount").getAsBigDecimal();
        String currency = data.get("currency").getAsString();

        if (!"successful".equalsIgnoreCase(status)
                || !responseTxRef.equals(txRef)
                || paidAmount.compareTo(reference.getAmount()) < 0
                || !currency.equalsIgnoreCase(reference.getCurrency())) {
            log.warn("⚠️ Redirect verification failed for txRef: {}", txRef);
            return false;
        }

        // 🔁 Step 3: Credit wallet
        Wallet wallet = reference.getUser().getWallet();
        if (wallet == null) {
            log.error("❌ No wallet found for user ID: {}", reference.getUser().getId());
            return false;
        }
        boolean credited = walletService.creditWalletByPhone(wallet.getUserId(), paidAmount, txRef, "Redirect-based verification");

        if (!credited) {
            log.error("❌ Wallet not credited. Aborting journal entry for txRef: {}", txRef);
            return false;
        }
        String accountCode = wallet.getAccountCode() != null ? wallet.getAccountCode() : "WALLET-FUNDING";
        //get from chart of account instead
        //

        // 🔁 Step 4: Post Journal Entry
        JournalEntryRequest journal = new JournalEntryRequest();
        journal.setAccountCode(accountCode);
        journal.setWalletId(wallet.getWalletId());
        journal.setTransactionType("CREDIT");
        journal.setAmount(paidAmount);
        journal.setDescription("Flutterwave Funding via Redirect");
        journal.setReference(txRef);
        journal.setDepartment("WALLET");
        journal.setBusinessUnit("TRUSTFORT");
        journal.setTransactionDate(LocalDateTime.now());

        try {
            String responses = accountingClient.postJournalEntryInternal(journal);  // ✅ Uses internal endpoint
            log.info("📘 Journal entry posted successfully for txRef {}: {}", txRef, responses);
        } catch (Exception je) {
            log.error("❌ Failed to post journal entry via Feign for txRef {}: {}", txRef, je.getMessage(), je);
        }


        // 🔁 Step 4: Mark reference as verified
        reference.setStatus(ReferenceStatus.VERIFIED);
        reference.setVerifiedAt(LocalDateTime.now());
        reference.setFlutterwaveTxId(transactionId);
        paymentReferenceRepository.save(reference);

        return true;
    } catch (Exception e) {
        log.error("❌ Redirect verification failed", e);
        return false;
    }

}

    public boolean reverifyAndCredit(PaymentReference reference) {
        try {
//            String url = "https://api.flutterwave.com/v3/transactions/" + reference.getTxRef() + "/verify";
            String url = "https://api.flutterwave.com/v3/transactions/verify_by_reference?tx_ref=" + reference.getTxRef();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(FLW_SECRET_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonObject data = JsonParser.parseString(response.getBody()).getAsJsonObject().getAsJsonObject("data");

            if (data == null) {
                log.warn("❌ No data returned from Flutterwave for txRef: {}", reference.getTxRef());
                return false;
            }

            // ✅ Extract verification fields

            String status = data.get("status").getAsString();
            BigDecimal paidAmount = data.get("amount").getAsBigDecimal();
            String currency = data.get("currency").getAsString();
            int flutterwaveTxId = data.get("id").getAsInt();

            // ✅ Compare with local reference
            if (!"successful".equalsIgnoreCase(status)
                    || paidAmount.compareTo(reference.getAmount()) < 0
                    || !currency.equalsIgnoreCase(reference.getCurrency())) {

                String reason = String.format("Status: %s, Amount: %s, Currency: %s", status, paidAmount, currency);

                PaymentFailureLog failureLog = PaymentFailureLog.builder()
                        .txRef(reference.getTxRef())
                        .reason(reason)
                        .expectedAmount(reference.getAmount())
                        .receivedAmount(paidAmount)
                        .expectedCurrency(reference.getCurrency())
                        .receivedCurrency(currency)
                        .statusReturned(status)
                        .userPhone(reference.getUser().getPhoneNumber())
                        .gateway("FLW")
                        .build();

                failureLogRepository.save(failureLog);


                log.warn("⚠️ Re-verification failed for txRef {}: {}", reference.getTxRef(), reason);
                return false;
            }

            boolean success = walletService.creditWalletByPhone(
                    reference.getUser().getPhoneNumber(),
                    paidAmount,
                    reference.getTxRef(),
                    "Reverified Flutterwave payment"
            );

            if (success) {
                reference.setStatus(ReferenceStatus.VERIFIED);
                reference.setVerifiedAt(LocalDateTime.now());
                reference.setFlutterwaveTxId(String.valueOf(flutterwaveTxId));
                paymentReferenceRepository.save(reference);

                log.info("✅ Wallet credited & reference updated for txRef: {}", reference.getTxRef());
                return true;
            } else {
                log.error("❌ Wallet credit failed for txRef: {}", reference.getTxRef());
                return false;
            }
        } catch (Exception e) {
            log.error("❌ Exception during Flutterwave re-verification for txRef {}: {}", reference.getTxRef(), e.getMessage());
            return false;
        }
    }

    @Scheduled(fixedDelay = 600000) // Runs every 10 minutes
    public void reconcilePendingReferences() {
        log.info("🔄 Running Flutterwave payment reconciliation...");

        // 1. Find all PENDING payment references
        List<PaymentReference> pendingRefs = paymentReferenceRepository.findByStatus(ReferenceStatus.PENDING);

        int total = 0;
        int credited = 0;

        for (PaymentReference ref : pendingRefs) {
            String txRef = ref.getTxRef();
            if (!webhookLogRepository.existsByTxRef(txRef)) continue;

            total++;
            if (reverifyAndCredit(ref)) {
                credited++;
            }
        }

        log.info("✅ Reconciliation complete. Attempted: {}, Credited: {}", total, credited);
    }



}
