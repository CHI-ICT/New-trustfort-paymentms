package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.Responses.*;
//import com.chh.trustfort.payment.component.AccountingClient;
import com.chh.trustfort.payment.component.ResponseCode;
import com.chh.trustfort.payment.component.WalletUtil;
import com.chh.trustfort.payment.dto.*;
import com.chh.trustfort.payment.dto.ConfirmBankTransferRequest;
import com.chh.trustfort.payment.enums.*;
import com.chh.trustfort.payment.exception.WalletException;
import com.chh.trustfort.payment.model.*;
import com.chh.trustfort.payment.payload.*;
import com.chh.trustfort.payment.repository.*;

import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.*;
import com.google.gson.Gson;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author DOfoleta
 */
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private static final Logger log = LoggerFactory.getLogger(WalletServiceImpl.class);
    private WalletService walletService;

    private final WalletRepository walletRepository;
    private final AppUserRepository appUserRepository;
    private final LedgerEntryRepository ledgerRepository;
    private final UsersService usersService;
    private final OtpService otpService;
    private final JournalPostingService journalPostingService;
    private final WalletUtil walletUtil;
    private final AuditLogService auditLogService;
    private final AesService aesService;
    private final PinService pinService;
    private final UsersRepository usersRepository;
    private final PendingBankTransferRepository pendingBankTransferRepository;
    private final AccountingClient accountingClient;
    private final NotificationService notificationService;
    private final MessageSource messageSource;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final PurchaseIntentRepository purchaseIntentRepository;
    private final PaymentReferenceRepository paymentReferenceRepository;
    private final PaystackPaymentService paystackPaymentService;
    private final FlutterwavePaymentService flutterwavePaymentService;


    private final Gson gson;
    @Autowired
    private HttpServletRequest httpServletRequest;

    private Wallet getWalletOrThrow(String walletId) {
        return walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new WalletException("Wallet not found for ID: " + walletId));
    }

    private String buildEncryptedResponse(String responseCode, String responseMessage, Object data, AppUser appUser) {
        Map<String, Object> response = new HashMap<>();
        response.put("responseCode", responseCode);
        response.put("responseMessage", responseMessage);
        if (data != null) {
            response.put("data", data);
        }
        return aesService.encrypt(gson.toJson(response), appUser);
    }



    @Override
    public List<WalletDTO> getWalletsByUserId(String userId) {
        log.info("🔎 Querying wallets for userId: {}", userId);
        Optional<Wallet> wallets = walletRepository.findByUserId(userId);

        return wallets.stream()
                .map(w -> WalletDTO.builder()
                        .walletId(w.getWalletId())
                        .userId(w.getUserId())
                        .email(w.getEmail())
                        .accountNumber(w.getAccountNumber())
                        .balance(w.getBalance())
                        .currency(w.getCurrency())
                        .status(w.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public String getWalletsByPhoneNumber(String phoneNumber, AppUser appUser) {
        com.chh.trustfort.payment.dto.wallet.GetWalletResponsePayload response = new com.chh.trustfort.payment.dto.wallet.GetWalletResponsePayload();
        response.setResponseCode("06");
        response.setResponseMessage("No wallets found");

        try {
            log.info("🔍 Attempting to fetch wallets for userId (phoneNumber): {}", phoneNumber);
            Optional<Wallet> wallets = walletRepository.findByUserId(phoneNumber); // ✅ Already using phoneNumber
//            log.info("📦 Wallets fetched from DB: {}", wallets.size()); // ✅ Safe logging
            if (wallets.isEmpty()) {
                log.warn("⚠️ No wallets found for phoneNumber: {}", phoneNumber);
                return aesService.encrypt(gson.toJson(response), appUser);
            }

            List<com.chh.trustfort.payment.dto.wallet.GetWalletResponsePayload.WalletDTO> walletDTOs = wallets.stream()
                    .map(w -> com.chh.trustfort.payment.dto.wallet.GetWalletResponsePayload.WalletDTO.builder()
                            .walletId(w.getWalletId())
                            .accountNumber(w.getAccountNumber())
                            .currency(w.getCurrency())
                            .balance(w.getBalance().toPlainString())
                            .phoneNumber(w.getPhoneNumber())
                            .email(w.getEmail())
                            .userId(w.getUserId())
                            .status(w.getStatus().name())
                            .build())
                    .collect(Collectors.toList());

            log.info("✅ WalletDTOs prepared: {}", gson.toJson(walletDTOs));

            response.setResponseCode("00");
            response.setResponseMessage("Wallets retrieved successfully");
            response.setWallets(walletDTOs);
        } catch (Exception ex) {
            log.error("❌ Error fetching wallets by phone number: {}", ex.getMessage(), ex);
            response.setResponseMessage("An unexpected error occurred");
        }

        String encryptedResponse = aesService.encrypt(gson.toJson(response), appUser);
        log.info("✅ Returning encrypted wallet response");
        return encryptedResponse;
    }

    @Override
    public String checkBalanceByPhoneNumber(String phoneNumber, AppUser appUser) {
        WalletBalanceResponse response = new WalletBalanceResponse();
        response.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());
        response.setMessage("No wallet found for the provided phone number");

        try {
            Optional<Wallet> wallets = walletRepository.findByUserId(phoneNumber);

            if (wallets.isEmpty()) {
                log.warn("❌ No wallet found for phoneNumber: {}", phoneNumber);
                return aesService.encrypt(gson.toJson(response), appUser);
            }

            Wallet wallet = wallets.get(); // 👈 First wallet only
            if (wallet.getStatus() == WalletStatus.SUSPENDED) {
                response.setMessage("Wallet is frozen and cannot be accessed");
                return aesService.encrypt(gson.toJson(response), appUser);
            }

            if (wallet.getStatus() == WalletStatus.CLOSED) {
                response.setMessage("Wallet is closed and cannot be accessed");
                return aesService.encrypt(gson.toJson(response), appUser);
            }

            response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
            response.setMessage("Wallet balance retrieved successfully");
            response.setUserId(wallet.getUserId());
            response.setBalance(wallet.getBalance());

            return aesService.encrypt(gson.toJson(response), appUser);

        } catch (Exception e) {
            log.error("❌ Error retrieving wallet balance for phoneNumber: {}", phoneNumber, e);
            response.setMessage("An error occurred while retrieving wallet balance");
            return aesService.encrypt(gson.toJson(response), appUser);
        }
    }

    public WalletBalanceResponse checkBalancePlain(String phoneNumber) {
        Wallet wallet = walletRepository.findByUserId(phoneNumber).stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        WalletBalanceResponse response = new WalletBalanceResponse();
        response.setResponseCode("00");
        response.setMessage("Success");
        response.setUserId(wallet.getUserId()); // ✅ Add this line
        response.setBalance(wallet.getBalance());
        response.setLedgerBalance(wallet.getLedgerBalance());
        return response;
    }

    @Override
    public boolean creditWalletByPhone(String phoneNumber, BigDecimal amount, String reference, String narration)
    {
        try {
            log.info("🔍 Verifying wallet for phone number: {}", phoneNumber);

            // Find wallet by userId (assumed to be phone number)
            Wallet wallet = walletRepository.findByUserId(phoneNumber).stream().findFirst().orElse(null);
            if (wallet == null) {
                log.warn("⚠️ Wallet not found for user: {}", phoneNumber);
                return false;
            }

            // Prevent duplicate credit using tx_ref
            if (ledgerRepository.findByTransactionReference(reference).isPresent()) {
                log.warn("⚠️ Duplicate transaction reference detected: {}", reference);
                return false;
            }

            // Credit wallet
            wallet.setBalance(wallet.getBalance().add(amount));
            wallet.setLedgerBalance(wallet.getLedgerBalance().add(amount));
            walletRepository.save(wallet);
//            walletRepository.updateUser(wallet);

            // Log to ledger
            WalletLedgerEntry ledger = new WalletLedgerEntry();
            ledger.setWallet(wallet);
            ledger.setWalletId(wallet.getWalletId());
            ledger.setAmount(amount);
            ledger.setTransactionReference(reference);
            ledger.setTransactionType(TransactionType.CREDIT);
            ledger.setStatus(TransactionStatus.COMPLETED);
            ledger.setNarration(narration != null ? narration : "Flutterwave wallet funding");

            ledgerRepository.save(ledger);






            log.info("✅ Wallet credited: {} | Amount: {} | Ref: {}", phoneNumber, amount, reference);
            return true;

        } catch (Exception e) {
            log.error("❌ Error crediting wallet for phone number {}: {}", phoneNumber, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String createWallet(CreateWalletRequestPayload requestPayload, AppUser appUser) {
        CreateWalletResponsePayload response = new CreateWalletResponsePayload();
        response.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());
        response.setResponseMessage(messageSource.getMessage("failed", null, Locale.ENGLISH));

        if (requestPayload == null || requestPayload.getData() == null) {
            log.warn("❌ Payload or user data is null");
            response.setResponseMessage("Invalid request payload.");
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        CreateWalletRequestPayload.UserData userData = requestPayload.getData();

        if (userData.getPhoneNumber() == null || userData.getEmailAddress() == null) {
            log.warn("⚠️ Phone number or email is missing");
            response.setResponseMessage(messageSource.getMessage("user.not.found", null, Locale.ENGLISH));
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        String phoneNumber = userData.getPhoneNumber();
        if (walletRepository.existsByUserId(phoneNumber)) {
            log.warn("⚠️ Wallet already exists for phone number: {}", phoneNumber);
            response.setResponseMessage(messageSource.getMessage("wallet.already.exists", null, Locale.ENGLISH));
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        // ✅ Generate wallet ID & serial
        String serialNumber = String.valueOf(System.currentTimeMillis()).substring(5);
        String walletId = "WAL-" + serialNumber;

        // ✅ Generate 10-digit account number
        String accountNumber = walletUtil.generateAccountNumber();
        String currency = requestPayload.getCurrency() != null ? requestPayload.getCurrency() : "NGN";

        Users users = usersRepository.findById(Long.valueOf(userData.getUserId())).orElse(null);
        if (users == null) {
            log.warn("❌ No Users entity found for DB userId: {}", userData.getUserId());
            response.setResponseMessage(messageSource.getMessage("user.not.found", null, Locale.ENGLISH));
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        Wallet wallet = new Wallet();
        wallet.setWalletId(walletId);
        wallet.setSerialNumber(Long.parseLong(serialNumber));
        wallet.setUserId(phoneNumber);
        wallet.setEmail(userData.getEmailAddress());
        wallet.setCurrency(currency);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setLedgerBalance(BigDecimal.ZERO);
        wallet.setStatus(WalletStatus.ACTIVE);
        wallet.setPhoneNumber(phoneNumber);
        wallet.setAccountNumber(accountNumber);
        wallet.setUsers(users);
        wallet.setAccountCode("2001106");


        wallet = walletRepository.createWallet(wallet);
        log.info("✅ Wallet created successfully: {}", wallet.getWalletId());

        // Validate account number (optional, for traceability)
        if (!walletUtil.validateAccountNumber(wallet.getAccountNumber())) {
            log.warn("⚠️ Account number validation failed: {}", wallet.getAccountNumber());
            response.setResponseMessage("Account number validation failed. Please contact support.");
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        // ✅ Build success response
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage(messageSource.getMessage("wallet.created.success", null, Locale.ENGLISH));
        response.setWalletId(wallet.getWalletId());

        AppUserActivity activity = new AppUserActivity();
        activity.setAppUser(appUser);
        activity.setActivity("CREATE_WALLET");
        activity.setDescription("Wallet created successfully with ID: " + wallet.getWalletId());
        activity.setIpAddress(appUser.getIpAddress());
        activity.setCreatedBy(userData.getUserName() != null ? userData.getUserName() : "SYSTEM");
        activity.setCreatedAt(LocalDateTime.now());
        activity.setStatus('S');

        appUserRepository.createUserActivity(activity);
        return aesService.encrypt(gson.toJson(response), appUser);
    }

    @Override
    public WalletBalanceResponse getWalletBalance(String walletId, String userId) {
        log.info("Fetching balance for Wallet ID: {}", userId);

        WalletBalanceResponse response = new WalletBalanceResponse();
        response.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());

        Wallet wallet = getWalletOrThrow(walletId);

        if (wallet.getStatus() == WalletStatus.SUSPENDED) {
            log.warn("Wallet is frozen: {}", userId);
            response.setMessage("Wallet is frozen and cannot be accessed");
            return response;
        }

        if (!wallet.getUserId().equals(userId)) {
            log.warn("Unauthorized access to wallet: {}", userId);
            response.setMessage("Unauthorized access to this wallet");
            return response;
        }

        if (wallet.getStatus() == WalletStatus.CLOSED) {
            response.setMessage("Wallet is closed and cannot be accessed");
            return response;
        }

        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setMessage("Balance retrieved successfully");
        response.setUserId(userId);
        response.setBalance(wallet.getBalance());

        return response;
    }

//    @Override
//    public ResponseEntity<List<LedgerEntry>> getTransactionHistory(String walletId, LocalDate startDate, LocalDate endDate, String userId) {
//        Wallet wallet = walletRepository.findByWalletId(walletId)
//                .orElseThrow(() -> new WalletException("Wallet not found for ID: " + walletId));
//
//        if (!wallet.getUserId().equals(userId)) {
//            throw new WalletException("Unauthorized access to this wallet");
//        }
//
//        List<LedgerEntry> entries = ledgerEntryRepository.findByWalletId(walletId);
//        return ResponseEntity.ok(entries); // ✅ wrap the list properly
//    }
@Override
public ResponseEntity<String> getTransactionHistory(
        String walletId, LocalDate startDate, LocalDate endDate, String userId, AppUser appUser) {

    Wallet wallet = walletRepository.findByWalletId(walletId)
            .orElseThrow(() -> new WalletException("Wallet not found for ID: " + walletId));

    if (!wallet.getUserId().equals(userId)) {
        return ResponseEntity.ok(buildEncryptedResponse("06", "Unauthorized access to this wallet", null, appUser));
    }

    List<WalletLedgerEntry> entries = ledgerEntryRepository.findByWalletId(userId);

    List<LedgerEntryDTO> dtos = entries.stream()
            .map(LedgerEntryDTO::fromEntity)
            .collect(Collectors.toList());

    return ResponseEntity.ok(buildEncryptedResponse("00", "Transaction history retrieved successfully", dtos, appUser));
}

    @Override
    @Transactional
    public String deductWalletForProductPurchase(ProductPurchaseDTO payload, AppUser appUser, AppUser ecred) {
        log.info("🛒 Initiating wallet deduction for product: {} | User: {} | Amount: {}",
                payload.getProductName(), payload.getUserId(), payload.getAmount());

        try {
            // ✅ Fetch user and wallet
            Users user = usersRepository.findByPhoneNumber(payload.getUserId())
                    .orElseThrow(() -> new WalletException("❌ User not found: " + payload.getUserId()));

            Wallet wallet = walletRepository.findByUserId(payload.getUserId())
                    .orElseThrow(() -> new WalletException("❌ Wallet not found for: " + payload.getUserId()));

            if (!wallet.getUsers().getId().equals(user.getId())) {
                return aesService.encrypt(gson.toJson(new ErrorResponse("Unauthorized access", "06")), ecred);
            }

            if (wallet.getStatus() != WalletStatus.ACTIVE) {
                return aesService.encrypt(gson.toJson(new ErrorResponse("Wallet is not active", "06")), ecred);
            }

            // ✅ Check balance
            if (wallet.getBalance().compareTo(payload.getAmount()) < 0) {
                return aesService.encrypt(gson.toJson(new ErrorResponse("Insufficient funds", "06")), ecred);
            }

            // ✅ Debit wallet
            UpdateWalletBalancePayload debitPayload = new UpdateWalletBalancePayload();
            debitPayload.setUserId(wallet.getUserId());
            debitPayload.setAmount(payload.getAmount().negate().doubleValue());

            String debitResultJson = updateWalletBalance(debitPayload, "INTERNAL-CALL", appUser);
            JsonObject debitResult = gson.fromJson(debitResultJson, JsonObject.class);

            if (!debitResult.get("responseCode").getAsString().equals(ResponseCode.SUCCESS.getResponseCode())) {
                return aesService.encrypt(debitResultJson, ecred);
            }

            // ✅ Save PurchaseIntent
            String txRef = "WALLET-" + System.currentTimeMillis();
            PurchaseIntent intent = PurchaseIntent.builder()
                    .userId(payload.getUserId())
                    .amount(payload.getAmount())
                    .stringifiedData(payload.getStringifiedData())
                    .status("COMPLETED")
                    .txRef(txRef)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            purchaseIntentRepository.save(intent);

            PaymentReference reference = new PaymentReference();
            reference.setTxRef(txRef);
            reference.setReferenceCode(txRef);
            reference.setAmount(payload.getAmount());
            reference.setCurrency("NGN");
            reference.setStatus(ReferenceStatus.VERIFIED);
            reference.setGateway("WALLET");
            reference.setVerifiedAt(LocalDateTime.now());
            reference.setUser(user);
            reference.setCustomerEmail(user.getEmail());
            reference.setType(PaymentType.PRODUCT);
            reference.setCreatedAt(LocalDateTime.now());

            paymentReferenceRepository.save(reference);


            // ✅ Wallet Ledger
            WalletLedgerEntry ledgerEntry = new WalletLedgerEntry();
            ledgerEntry.setWalletId(wallet.getUserId());
            ledgerEntry.setTransactionType(TransactionType.DEBIT);
            ledgerEntry.setAmount(payload.getAmount());
            ledgerEntry.setStatus(TransactionStatus.COMPLETED);
            ledgerEntry.setDescription("Product Purchase: " + payload.getProductName());
            ledgerEntryRepository.save(ledgerEntry);

            // ✅ Double-entry journal
            LocalDateTime now = LocalDateTime.now();

            JournalEntryRequest debitEntry = new JournalEntryRequest();
            debitEntry.setAccountCode(wallet.getAccountCode()); // 🔴 DEBIT from wallet
            debitEntry.setAmount(payload.getAmount());
            debitEntry.setDescription("Product purchase from wallet: " + payload.getProductName());
            debitEntry.setTransactionType("DEBIT");
            debitEntry.setDepartment("Wallet");
            debitEntry.setBusinessUnit("Retail");
            debitEntry.setTransactionDate(now);
            accountingClient.postJournalEntryInternal(debitEntry);

            JournalEntryRequest creditEntry = new JournalEntryRequest();
            creditEntry.setAccountCode("2001106"); // 🔵 CREDIT to product revenue account (e.g. PRODUCT_SALES)
            creditEntry.setAmount(payload.getAmount());
            creditEntry.setDescription("Product sale credited from wallet of " + wallet.getUserId());
            creditEntry.setTransactionType("CREDIT");
            creditEntry.setDepartment("Sales");
            creditEntry.setBusinessUnit("Retail");
            creditEntry.setTransactionDate(now);
            accountingClient.postJournalEntryInternal(creditEntry);

            // ✅ Response
            SuccessResponse success = new SuccessResponse();
            success.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
            success.setResponseMessage("Product purchase processed from wallet");
            success.setNewBalance(debitResult.get("newBalance").getAsBigDecimal());

            return aesService.encrypt(gson.toJson(success), ecred);

        } catch (Exception ex) {
            log.error("❌ Error in wallet product purchase: {}", ex.getMessage(), ex);
            return aesService.encrypt(gson.toJson(new ErrorResponse("Internal error: " + ex.getMessage(), "99")), ecred);
        }
    }





    @Override
    @Transactional
    public String transferFunds(FundsTransferRequestPayload payload, String idToken, AppUser appUser, AppUser ecred) {
        log.info("🔁 Initiating fund transfer from: {} to: {} amount: {}",
                payload.getSenderUserId(), payload.getReceiverUserId(), payload.getAmount());

        // ❌ Check if sender has any wallet at all
        if (!walletRepository.existsByUserId(payload.getSenderUserId())) {
            log.warn("❌ No wallet found for sender phone number: {}", payload.getSenderUserId());
            return aesService.encrypt(gson.toJson(
                    new ErrorResponse("You do not have an active wallet. Please create one first.", "06")
            ), ecred);
        }

        // ❌ Check if receiver has any wallet
        if (!walletRepository.existsByUserId(payload.getReceiverUserId())) {
            log.warn("❌ No wallet found for receiver phone number: {}", payload.getReceiverUserId());
            return aesService.encrypt(gson.toJson(
                    new ErrorResponse("Receiver does not have an active wallet.", "06")
            ), ecred);
        }


        try {
            // ✅ Lookup sender user by phone number
            Users user = usersRepository.findByPhoneNumber(payload.getSenderUserId())
                    .orElseThrow(() -> new WalletException("❌ User entity not found for phone number: " + payload.getSenderUserId()));

            // ✅ Fetch sender wallet
            Wallet senderWallet = walletRepository.findByUserId(payload.getSenderUserId())
                    .orElseThrow(() -> new WalletException("❌ Sender wallet not found: " + payload.getSenderUserId()));


            // ✅ Verify wallet belongs to user
            if (!senderWallet.getUsers().getId().equals(user.getId())) {
                log.warn("❌ Unauthorized access. Wallet owner ID: {}, User ID: {}",
                        senderWallet.getUsers().getId(), user.getId());

                return aesService.encrypt(gson.toJson(
                        new ErrorResponse("Unauthorized access to sender wallet", "06")
                ), ecred);
            }

            log.info("✅ Authorized user={} owns wallet={}", user.getPhoneNumber(), senderWallet.getWalletId());

            // 🔐 Check if sender wallet is active
            if (senderWallet.getStatus() != WalletStatus.ACTIVE) {
                log.warn("❌ Sender wallet is not active. walletId={}, status={}", senderWallet.getWalletId(), senderWallet.getStatus());

                return aesService.encrypt(gson.toJson(
                        new ErrorResponse("Sender wallet is currently " + senderWallet.getStatus().name().toLowerCase(),
                                ResponseCode.FAILED_TRANSACTION.getResponseCode())
                ), ecred);
            }

            log.info("✅ Authorized user={} owns wallet={}", user.getPhoneNumber(), senderWallet.getWalletId());



            // ✅ Fetch receiver wallet
            Wallet receiverWallet = walletRepository.findByUserId(payload.getReceiverUserId())
                    .orElseThrow(() -> new WalletException("❌ Receiver wallet not found: " + payload.getReceiverUserId()));

            // 🔐 Check if receiver wallet is active
            if (receiverWallet.getStatus() != WalletStatus.ACTIVE) {
                log.warn("❌ Receiver wallet is not active. walletId={}, status={}", receiverWallet.getWalletId(), receiverWallet.getStatus());

                return aesService.encrypt(gson.toJson(
                        new ErrorResponse("Receiver wallet is currently " + receiverWallet.getStatus().name().toLowerCase(),
                                ResponseCode.FAILED_TRANSACTION.getResponseCode())
                ), ecred);
            }


            // ✅ Check balance
            BigDecimal transferAmount = payload.getAmount();
            if (senderWallet.getBalance().compareTo(transferAmount) < 0) {
                log.warn("❌ Insufficient funds. Wallet balance: {}, Requested: {}",
                        senderWallet.getBalance(), transferAmount);

                return aesService.encrypt(gson.toJson(
                        new ErrorResponse("Insufficient funds in sender wallet", ResponseCode.FAILED_TRANSACTION.getResponseCode())
                ), ecred);
            }

            // ✅ Perform debit
            UpdateWalletBalancePayload debitPayload = new UpdateWalletBalancePayload();
            debitPayload.setUserId(senderWallet.getUserId());
            debitPayload.setAmount(transferAmount.negate().doubleValue());

            String debitResultJson = updateWalletBalance(debitPayload, idToken, appUser);
            JsonObject debitResult = gson.fromJson(debitResultJson, JsonObject.class);
            if (!debitResult.get("responseCode").getAsString().equals(ResponseCode.SUCCESS.getResponseCode())) {
                log.warn("❌ Debit failed: {}", debitResultJson);
                return aesService.encrypt(debitResultJson, ecred);
            }

            // ✅ Perform credit
            UpdateWalletBalancePayload creditPayload = new UpdateWalletBalancePayload();
            creditPayload.setUserId(receiverWallet.getUserId());
            creditPayload.setAmount(transferAmount.doubleValue());

            String creditResultJson = updateWalletBalance(creditPayload, idToken, appUser);
            JsonObject creditResult = gson.fromJson(creditResultJson, JsonObject.class);
            if (!creditResult.get("responseCode").getAsString().equals(ResponseCode.SUCCESS.getResponseCode())) {
                log.warn("❌ Credit failed: {}", creditResultJson);
                return aesService.encrypt(creditResultJson, ecred);
            }

            // ✅ Ledger entry for sender
            WalletLedgerEntry senderEntry = new WalletLedgerEntry();
            senderEntry.setWalletId(senderWallet.getUserId());
            senderEntry.setTransactionType(TransactionType.DEBIT);
            senderEntry.setAmount(transferAmount);
            senderEntry.setStatus(TransactionStatus.COMPLETED);
            senderEntry.setDescription("Transfer to " + receiverWallet.getUserId());
            ledgerEntryRepository.save(senderEntry);

            // ✅ Ledger entry for receiver
            WalletLedgerEntry receiverEntry = new WalletLedgerEntry();
            receiverEntry.setWalletId(receiverWallet.getUserId());
            receiverEntry.setTransactionType(TransactionType.CREDIT);
            receiverEntry.setAmount(transferAmount);
            receiverEntry.setStatus(TransactionStatus.COMPLETED);
            receiverEntry.setDescription("Transfer from " + senderWallet.getUserId());
            ledgerEntryRepository.save(receiverEntry);

            // ✅ Post journal entries
            LocalDateTime now = LocalDateTime.now();
            JournalEntryRequest debitEntry = new JournalEntryRequest();
            debitEntry.setAccountCode("2001106");
            debitEntry.setAmount(transferAmount);
            debitEntry.setDescription("Transfer to " + receiverWallet.getUserId());
            debitEntry.setTransactionType("DEBIT");
            debitEntry.setDepartment("Wallet");
            debitEntry.setBusinessUnit("Retail");
            debitEntry.setTransactionDate(now);
            accountingClient.postJournalEntryInternal(debitEntry);

            JournalEntryRequest creditEntry = new JournalEntryRequest();
            creditEntry.setAccountCode("2001106");
            creditEntry.setAmount(transferAmount);
            creditEntry.setDescription("Transfer from " + senderWallet.getUserId());
            creditEntry.setTransactionType("CREDIT");
            creditEntry.setDepartment("Wallet");
            creditEntry.setBusinessUnit("Retail");
            creditEntry.setTransactionDate(now);
            accountingClient.postJournalEntryInternal(creditEntry);

            // ✅ Build and return success response
            SuccessResponse response = new SuccessResponse();
            response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
            response.setResponseMessage("Transfer processed successfully");
            response.setNewBalance(debitResult.get("newBalance").getAsBigDecimal());

            log.info("✅ Transfer completed successfully.");
            return aesService.encrypt(gson.toJson(response), ecred);

        } catch (Exception ex) {
            log.error("💥 Exception in transferFunds: {}", ex.getMessage(), ex);

            return aesService.encrypt(gson.toJson(
                    new ErrorResponse("Internal server error: " + ex.getClass().getSimpleName(), "99")
            ), ecred);
        }
    }

    @Override
    @Transactional
    public String initiateWalletFunding(FundWalletRequestPayload payload, AppUser appUser) {
        PaymentMethod method = payload.getPaymentMethod();
        String phoneNumber = appUser.getPhoneNumber();

        log.info("⚙️ Initiating wallet funding via method: {}", method);
        log.info("📞 Sender phone number (from AppUser): {}", phoneNumber);

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new WalletException("❌ Sender phone number is missing or invalid.");
        }

        // ✅ Find sender wallet using phone from logged-in AppUser
        Wallet senderWallet = walletRepository.findByUserId(phoneNumber)
                .orElse(null);
        if (senderWallet == null || senderWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new WalletException("❌ Sender does not have an active wallet.");
        }

        Wallet receiverWallet = walletRepository.findByUserId(payload.getUserId())
                .orElse(null);
        if (receiverWallet == null || receiverWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new WalletException("❌ Receiver does not have an active wallet.");
        }

        log.info("👥 Sender Wallet ID: {} | Receiver Wallet ID: {}", senderWallet.getUserId(), receiverWallet.getUserId());

        switch (method) {
            case WALLET:
                FundsTransferRequestPayload transferPayload = new FundsTransferRequestPayload();
                transferPayload.setSenderUserId(senderWallet.getUserId());
                transferPayload.setReceiverUserId(receiverWallet.getUserId());
                transferPayload.setAmount(payload.getAmount());
                transferPayload.setNarration(payload.getNarration());

                // ✅ Use real logged-in AppUser for encryption
                return transferFunds(transferPayload, "INTERNAL-CALL", appUser, appUser);

            case PAYSTACK:
                return paystackPaymentService.initiatePaystackPayment(payload, appUser);

            case FLUTTERWAVE:
                return flutterwavePaymentService.initiateFlutterwavePayment(payload, appUser);

            case BANK_TRANSFER:
                return initiateBankTransferFunding(payload, appUser);

            case OPEN_BANKING:
                return fundViaOpenBanking(payload, appUser);

            default:
                String errorMsg = "❌ Invalid payment method: " + method;
                log.warn(errorMsg);
                return aesService.encrypt(gson.toJson(new ErrorResponse(errorMsg, "91")), appUser);
        }
    }

    private String fundViaOpenBanking(FundWalletRequestPayload payload, AppUser appUser) {
        try {
            // ✅ Try Paystack first
            return paystackPaymentService.initiatePaystackPayment(payload, appUser);
        } catch (Exception paystackEx) {
            log.warn("⚠️ Paystack failed: {}. Trying Flutterwave...", paystackEx.getMessage());

            try {
                return flutterwavePaymentService.initiateFlutterwavePayment(payload, appUser);
            } catch (Exception flutterEx) {
                log.error("❌ Both gateways failed. Paystack={}, Flutterwave={}",
                        paystackEx.getMessage(), flutterEx.getMessage());
                return aesService.encrypt(gson.toJson(
                        new ErrorResponse("Payment initiation failed on both Paystack and Flutterwave", "91")
                ), appUser);
            }
        }
    }

    public String initiateBankTransferFunding(FundWalletRequestPayload payload, AppUser appUser) {
        String reference = "BTF-" + System.currentTimeMillis(); // Bank Transfer Funding Ref

        // 🔐 Save pending record for later reconciliation
        PendingBankTransfer pending = PendingBankTransfer.builder()
                .userId(payload.getUserId())
                .reference(reference)
                .userId(appUser.getPhoneNumber())
                .amount(payload.getAmount())
                .currency(payload.getCurrency())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        pendingBankTransferRepository.save(pending);

        // 📋 Send back bank transfer details
        Map<String, Object> response = new HashMap<>();
        response.put("status", "pending");
        response.put("reference", reference);
        response.put("message", "Kindly transfer to the bank details below and include the reference.");
        response.put("bankDetails", Map.of(
                "bankName", "Access Bank",
                "accountNumber", "0724401480",
                "accountName", "Ajayi Oloruntobi For Trustfort",
                "reference", reference,
                "amount", payload.getAmount()
        ));

        return aesService.encrypt(gson.toJson(response), appUser);
    }

    //    @Override
//    @Transactional
//    public String fundWalletInternally(FundWalletRequestPayload payload, String userId, String emailAddress) {
//        log.info("🔄 Funding wallet for user ID: {}", userId);
//
//        // 🔍 Step 1: Fetch wallet and validate
//        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
//                .orElseThrow(() -> new WalletException("❌ Wallet not found for ID: " + payload.getWalletId()));
//
//        if (wallet.getStatus() == WalletStatus.SUSPENDED || wallet.getStatus() == WalletStatus.CLOSED) {
//            return aesService.encrypt(gson.toJson(new ErrorResponse("Wallet is not active", ResponseCode.FAILED_TRANSACTION.getResponseCode())), new AppUser());
//        }
//
//        if (!wallet.getUserId().equals(userId)) {
//            return aesService.encrypt(gson.toJson(new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode())), new AppUser());
//        }
//
//        // 💳 Step 2: Credit wallet
//        BigDecimal creditAmount = payload.getAmount();
//        wallet.setBalance(wallet.getBalance().add(creditAmount));
//        walletRepository.updateUser(wallet);
//
//        String internalRef = "MWF-" + System.currentTimeMillis();
//        // 🧾 Step 3: Save Ledger Entry
//        WalletLedgerEntry ledgerEntry = WalletLedgerEntry.builder()
//                .walletId(wallet.getWalletId())
//                .transactionType(TransactionType.CREDIT)
//                .amount(creditAmount)
//                .status(TransactionStatus.COMPLETED)
//                .description("Wallet Funding")
//                .reference(internalRef)
//                .build();
//
//        ledgerEntryRepository.save(ledgerEntry);
//
//        // 📒 Step 4: Post Journal Entry to Accounting
//        JournalEntryRequest journal = new JournalEntryRequest();
//        journal.setAccountCode(wallet.getAccountCode() != null ? wallet.getAccountCode() : "WALLET-FUNDING");
//        journal.setAmount(creditAmount);
//        journal.setDescription("Wallet Funding");
//        journal.setTransactionType("CREDIT");
//        journal.setReference(internalRef);
//        journal.setDepartment("WALLET");
//        journal.setBusinessUnit("TRUSTFORT");
//        journal.setTransactionDate(LocalDateTime.now());
//        journal.setWalletId(wallet.getWalletId());
//
//        try {
//            accountingClient.postJournalEntryInternal(journal);
//            log.info("📘 Journal entry posted successfully for manual wallet funding.");
//        } catch (Exception e) {
//            log.error("❌ Failed to post journal entry: {}", e.getMessage(), e);
//        }
//
//        // 📧 Step 5: Send email notification
//        notificationService.sendEmail(
//                emailAddress,
//                "🔺 Wallet Funded",
//                "Your wallet was credited with ₦" + creditAmount + " via manual funding."
//        );
//
//        // ✅ Step 6: Build success response
//        SuccessResponse response = new SuccessResponse();
//        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//        response.setResponseMessage("Wallet funded successfully");
//        response.setNewBalance(wallet.getBalance());
//
//        log.info("✅ Wallet ID {} funded successfully. New Balance: {}", wallet.getWalletId(), wallet.getBalance());
//
//        return aesService.encrypt(gson.toJson(response), new AppUser());
//        // Return encrypted response for external use
//    }
//    @Override
//    public String fetchAllWallets(String userId, AppUser user) {
//        Optional<Wallet> wallets = walletRepository.findByUserId(userId);
//
//        if (wallets.isEmpty()) {
//            ErrorResponse errorResponse = new ErrorResponse(
//                    "No wallets found for user ID: " + userId,
//                    ResponseCode.FAILED_TRANSACTION.getResponseCode()
//            );
//            return aesService.encrypt(gson.toJson(errorResponse), user);
//        }
//
//        WalletResponse walletResponse = new WalletRespons
//                ResponseCode.SUCCESS.getResponseCode(),
//                "Wallets retrieved successfully",
//                (Wallet) wallets
//        );
//
//        return aesService.encrypt(gson.toJson(walletResponse), user);
//    }

//    @Override
//    @Transactional
//    public String withdrawFunds(WithdrawFundsRequestPayload payload, String userId, String email, String idToken, AppUser appUser) {
//        log.info("💸 Processing withdrawal for wallet ID: {}", payload.getWalletId());
//
//        // 🔐 Step 1: Validate User
//        Users user = usersRepository.findByEmail(email)
//                .orElseThrow(() -> new WalletException("User not found: " + email));
//
//        if (!otpService.validateOtp(Long.valueOf(user.getId()), String.valueOf(payload.getOtpCode()), "WITHDRAW_FUNDS")) {
//            return aesService.encrypt(gson.toJson(new ErrorResponse("Invalid or expired OTP", ResponseCode.FAILED_TRANSACTION.getResponseCode())), appUser);
//        }
//
//        // 🔍 Step 2: Fetch Wallet and Validate
//        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
//                .orElseThrow(() -> new WalletException("Wallet not found for ID: " + payload.getWalletId()));
//
//        if (!wallet.getUserId().equals(userId)) {
//            return aesService.encrypt(gson.toJson(new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode())), appUser);
//        }
//
//        if (wallet.getStatus() != WalletStatus.ACTIVE) {
//            return aesService.encrypt(gson.toJson(new ErrorResponse("Wallet is " + wallet.getStatus(), ResponseCode.FAILED_TRANSACTION.getResponseCode())), appUser);
//        }
//
//        // 🔒 Step 3: Fraud Check & Balance Validation
//        BigDecimal withdrawalAmount = payload.getAmount();
//        if (fraudDetectionService.isFraudulentWithdrawal(user, withdrawalAmount)) {
//            return aesService.encrypt(gson.toJson(new ErrorResponse("Suspicious transaction: Limit exceeded", ResponseCode.FAILED_TRANSACTION.getResponseCode())), appUser);
//        }
//
//        if (wallet.getBalance().compareTo(withdrawalAmount) < 0) {
//            return aesService.encrypt(gson.toJson(new ErrorResponse("Insufficient funds", ResponseCode.FAILED_TRANSACTION.getResponseCode())), appUser);
//        }
//
//        // ⚙️ Step 4: System Parameters
//        String debitAccount = systemParameterService.getValue("WALLET_WITHDRAWAL_DEBIT_ACCOUNT");
//        String creditAccount = systemParameterService.getValue("WALLET_WITHDRAWAL_CREDIT_ACCOUNT");
//        String department = systemParameterService.getValue("WALLET_DEPARTMENT");
//        String businessUnit = systemParameterService.getValue("WALLET_BUSINESS_UNIT");
//        String settlementAccountNo = systemParameterService.getValue("WALLET_SETTLEMENT_ACCOUNT");
//
//        // 💳 Step 5: Debit Wallet
//        wallet.setBalance(wallet.getBalance().subtract(withdrawalAmount));
//        walletRepository.updateUser(wallet);
//
//        // 📘 Step 6: Journal Entries
//        JournalEntryRequest debitEntry = JournalEntryRequest.builder()
//                .accountCode(debitAccount)
//                .amount(withdrawalAmount)
//                .transactionType("DEBIT")
//                .description("Wallet Withdrawal")
//                .department(department)
//                .businessUnit(businessUnit)
//                .transactionDate(LocalDateTime.now())
//                .walletId(wallet.getWalletId())
//                .build();
//        accountingClient.postJournalEntryInternal(debitEntry);
//
//        JournalEntryRequest creditEntry = JournalEntryRequest.builder()
//                .accountCode(creditAccount)
//                .amount(withdrawalAmount)
//                .transactionType("CREDIT")
//                .description("Transfer to Bank")
//                .department(department)
//                .businessUnit(businessUnit)
//                .transactionDate(LocalDateTime.now())
//                .walletId(wallet.getWalletId())
//                .build();
//        accountingClient.postJournalEntryInternal(creditEntry);
//
//        // 🔁 Step 7: Create Recipient (Paystack)
//        String recipientCode;
//        try {
//            recipientCode = paystackTransferService.createRecipient(
//                    payload.getAccountName(), payload.getAccountNumber(), payload.getBankCode());
//        } catch (Exception e) {
//            rollbackWallet(wallet, withdrawalAmount, idToken, appUser);
//            return aesService.encrypt(gson.toJson(new ErrorResponse("Recipient creation failed", ResponseCode.FAILED_TRANSACTION.getResponseCode())), appUser);
//        }
//
//        // 🚀 Step 8: Initiate Transfer
//        String transferCode;
//        try {
//            transferCode = paystackTransferService.initiateTransfer(
//                    withdrawalAmount, recipientCode, "Wallet withdrawal for " + user.getUserName());
//        } catch (Exception e) {
//            rollbackWallet(wallet, withdrawalAmount, idToken, appUser);
//            return aesService.encrypt(gson.toJson(new ErrorResponse("Transfer failed", ResponseCode.FAILED_TRANSACTION.getResponseCode())), appUser);
//        }
//
//        // 📒 Step 9: Ledger Entry
//        WalletLedgerEntry ledgerEntry = WalletLedgerEntry.builder()
//                .walletId(wallet.getWalletId())
//                .transactionType(TransactionType.DEBIT)
//                .amount(withdrawalAmount)
//                .status(TransactionStatus.PENDING)
//                .description("Wallet Withdrawal - Pending Settlement")
//                .reference(transferCode)
//                .build();
//        ledgerEntryRepository.save(ledgerEntry);
//
//        // 🏦 Step 10: Update Settlement Account
//        SettlementAccount settlement = settlementAccountRepository.findByAccountNumber(settlementAccountNo);
//        if (settlement == null) {
//            settlement = new SettlementAccount();
//            settlement.setAccountNumber(settlementAccountNo);
//            settlement.setBalance(BigDecimal.ZERO);
//        }
//        settlement.setBalance(settlement.getBalance().add(withdrawalAmount));
//        settlementAccountRepository.save(settlement);
//
//        // 🔄 Step 11: Final Transfer to Bank (Mock FCMB)
//        boolean fcmbSuccess = fcmbIntegrationService.transferFunds(settlementAccountNo, withdrawalAmount);
//        ledgerEntry.setStatus(fcmbSuccess ? TransactionStatus.COMPLETED : TransactionStatus.FAILED);
//        ledgerEntryRepository.save(ledgerEntry);
//
//        if (!fcmbSuccess) {
//            rollbackWallet(wallet, withdrawalAmount, idToken, appUser);
//        }
//
//        // 📩 Step 12: Send Notification
//        notificationService.sendEmail(email,
//                "🔻 Debit Alert - Wallet Withdrawal",
//                "Your wallet was debited with ₦" + withdrawalAmount + "\nRef: " + transferCode);
//
//        // ✅ Step 13: Final Encrypted Response
//        SuccessResponse response = new SuccessResponse();
//        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//        response.setResponseMessage(fcmbSuccess ? "Withdrawal successful" : "Transfer failed, funds refunded");
//        response.setNewBalance(wallet.getBalance());
//
//        return aesService.encrypt(gson.toJson(response), appUser);
//    }

    @Override
    @Transactional
    public String fundWalletInternally(FundWalletRequestPayload payload, AppUser appUser) {
        log.info("🔄 Funding wallet for user ID: {}", appUser.getPhoneNumber());

        // 🔍 Step 1: Fetch wallet and validate
        Wallet wallet = walletRepository.findByUserId(payload.getUserId())
                .orElseThrow(() -> new WalletException("❌ Wallet not found for ID: " + payload.getUserId()));

        if (wallet.getStatus() == WalletStatus.SUSPENDED || wallet.getStatus() == WalletStatus.CLOSED) {
            return aesService.encrypt(gson.toJson(new ErrorResponse("Wallet is not active", ResponseCode.FAILED_TRANSACTION.getResponseCode())), appUser);
        }

        if (!wallet.getUserId().equals(appUser.getPhoneNumber())) {
            return aesService.encrypt(gson.toJson(new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode())), appUser);
        }

        // 💳 Step 2: Credit wallet
        BigDecimal creditAmount = payload.getAmount();
        wallet.setBalance(wallet.getBalance().add(creditAmount));
        walletRepository.updateUser(wallet);

        String internalRef = "MWF-" + System.currentTimeMillis();

        // 🧾 Step 3: Save Ledger Entry
        WalletLedgerEntry ledgerEntry = WalletLedgerEntry.builder()
                .walletId(wallet.getWalletId())
                .transactionType(TransactionType.CREDIT)
                .amount(creditAmount)
                .status(TransactionStatus.COMPLETED)
                .description("Wallet Funding")
                .reference(internalRef)
                .build();

        ledgerEntryRepository.save(ledgerEntry);

        // 📒 Step 4: Post Journal Entry to Accounting
        // ✅ Journal Entry
        journalPostingService.postDoubleEntry(creditAmount, internalRef, wallet, "Wallet Credit ");


        // 📧 Step 5: Send email notification
//        notificationService.sendEmail(
//                appUser.getEmail(),
//                "🔺 Wallet Funded",
//                "Your wallet was credited with ₦" + creditAmount + " via manual funding."
//        );

        // ✅ Step 6: Build success response
        SuccessResponse response = new SuccessResponse();
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("Wallet funded successfully");
        response.setNewBalance(wallet.getBalance());

        log.info("✅ Wallet ID {} funded successfully. New Balance: {}", wallet.getWalletId(), wallet.getBalance());

        return aesService.encrypt(gson.toJson(response), appUser);
    }

    @Override
    @Transactional
    public String freezeWallet(String requestPayload, String idToken, AppUser appUser) {
        FreezeWalletRequestPayload payload = gson.fromJson(requestPayload, FreezeWalletRequestPayload.class);

        // 🔍 Fetch wallet
        Wallet wallet = walletRepository.findByUserId(payload.getUserId())
                .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getUserId()));

//        // 🔐 Ownership check (FIXED to compare phone number)
//        if (!wallet.getUserId().equals(appUser.getPhoneNumber())) {
//            log.warn("❌ Unauthorized freeze attempt. walletUserId={}, requestUser={}", wallet.getUserId(), appUser.getPhoneNumber());
//            return gson.toJson(new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
//        }

        // ⛔ Already frozen?
        if (wallet.getStatus() == WalletStatus.SUSPENDED) {
            return gson.toJson(new ErrorResponse("Wallet is already frozen", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        // ❄️ Freeze wallet
        wallet.setStatus(WalletStatus.SUSPENDED);
        wallet.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        walletRepository.updateUser(wallet);

        // 📧 Notify
        String subject = "🔒 Wallet Frozen";
        String message = "Your wallet with ID " + payload.getUserId() + " has been successfully frozen.";
//        notificationService.sendEmail(appUser.getEmail(), subject, message);

        // ✅ Success
        SuccessResponse response = new SuccessResponse();
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("Wallet frozen successfully");
        response.setNewBalance(wallet.getBalance());

        return gson.toJson(response);
    }

    @Override
    @Transactional
    public String unfreezeWallet(UnfreezeWalletRequestPayload payload, String idToken, AppUser appUser) {
        Wallet wallet = walletRepository.findByUserId(payload.getUserId())
                .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getUserId()));

//        // 🔐 Ownership check (FIXED to compare phone number)
//        if (!wallet.getUserId().equals(appUser.getPhoneNumber())) {
//            log.warn("❌ Unauthorized freeze attempt. walletUserId={}, requestUser={}", wallet.getUserId(), appUser.getPhoneNumber());
//            return gson.toJson(new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
//        }

        if (wallet.getStatus() == WalletStatus.ACTIVE) {
            return gson.toJson(new ErrorResponse("Wallet is already active", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        wallet.setStatus(WalletStatus.ACTIVE);
        wallet.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        walletRepository.updateUser(wallet);

//        notificationService.sendEmail(appUser.getEmail()
//                , "✅ Wallet Unfrozen",
//                "Your wallet with ID " + payload.getUserId() + " has been reactivated.");

        SuccessResponse response = new SuccessResponse();
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("Wallet unfrozen successfully");
        response.setNewBalance(wallet.getBalance());

        return gson.toJson(response);
    }

    @Override
    @Transactional
    public String closeWallet(CloseWalletRequestPayload payload, String idToken, AppUser appUser) {
        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
                .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getWalletId()));

        if (!wallet.getUserId().equals(appUser.getId())) {
            return gson.toJson(new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        if (wallet.getStatus() == WalletStatus.CLOSED) {
            return gson.toJson(new ErrorResponse("Wallet is already closed", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        wallet.setStatus(WalletStatus.CLOSED);
        wallet.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        walletRepository.updateUser(wallet);

        notificationService.sendEmail(appUser.getEmail(), "❌ Wallet Closed",
                "Your wallet with ID " + payload.getWalletId() + " has been permanently closed.");

        SuccessResponse response = new SuccessResponse();
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("Wallet closed successfully");
        response.setNewBalance(wallet.getBalance());

        return gson.toJson(response);
    }

    @Override
    @Transactional
    public String lockFunds(LockFundsRequestPayload payload, String idToken, AppUser appUser) {
        Wallet wallet = walletRepository.findByUserId(payload.getUserId())
                .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getUserId()));

        // 🔐 Step 2.1: Wallet status check
        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            log.warn("❌ Wallet is not active. walletId={}, status={}", wallet.getUserId(), wallet.getStatus());
            throw new RuntimeException("❌ Cannot initiate payment: Wallet is currently " + wallet.getStatus().name().toLowerCase());
        }

        if (wallet.getStatus() == WalletStatus.LOCKED) {
            return gson.toJson(new ErrorResponse("Wallet is already locked", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        wallet.setStatus(WalletStatus.LOCKED);
        wallet.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        walletRepository.updateUser(wallet);

//        notificationService.sendEmail(
//                appUser.getEmail(),
//                "🔒 Wallet Locked",
//                "Your wallet with ID " + payload.getUserId() + " has been locked. Contact support if this was not initiated by you."
//        );

        SuccessResponse response = new SuccessResponse();
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("Funds locked successfully");
        response.setNewBalance(wallet.getBalance());

        return gson.toJson(response);
    }

    @Override
    @Transactional
    public String unlockFunds(UnlockFundsRequestPayload payload, String idToken, AppUser appUser) {
        Wallet wallet = walletRepository.findByUserId(payload.getUserId())
                .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getUserId()));

        // ✅ Only allow unlocking if status is LOCKED
        if (wallet.getStatus() != WalletStatus.LOCKED) {
            log.warn("⚠️ Wallet is not locked. walletId={}, status={}", wallet.getUserId(), wallet.getStatus());
            return gson.toJson(new ErrorResponse("Wallet is not locked", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        // 🔓 Unlock the wallet
        wallet.setStatus(WalletStatus.ACTIVE);
        wallet.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        walletRepository.updateUser(wallet);

        // ✅ Optional: send notification
//    notificationService.sendEmail(
//        appUser.getEmail(),
//        "🔓 Wallet Unlocked",
//        "Your wallet with ID " + payload.getUserId() + " has been unlocked and is now active."
//    );

        SuccessResponse response = new SuccessResponse();
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("Funds unlocked successfully");
        response.setNewBalance(wallet.getBalance());

        return gson.toJson(response);
    }


    @Override
    @Transactional
    public String updateWalletBalance(UpdateWalletBalancePayload payload, String idToken, AppUser appUser) throws WalletException {
        Wallet wallet = walletRepository.findByUserId(payload.getUserId())
                .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getUserId()));

//        if (!wallet.getUserId().equals(appUser.getId())) {
//            return gson.toJson(new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
//        }
        log.info("ℹ️ Updating balance for walletId={}, userId={}", wallet.getWalletId(), wallet.getUserId());

        BigDecimal newBalance = wallet.getBalance().add(BigDecimal.valueOf(payload.getAmount()));

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            return gson.toJson(new ErrorResponse("Insufficient funds", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        wallet.setBalance(newBalance);
        wallet.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        walletRepository.updateUser(wallet);

        SuccessResponse response = new SuccessResponse();
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("Wallet balance updated successfully");
        response.setNewBalance(wallet.getBalance());

        return gson.toJson(response);
    }

    @Override
    @Transactional
    public String processWebhookDeposit(FcmbWebhookPayload payload, String idToken, AppUser appUser) {
        log.info("Processing FCMB webhook deposit for reference: {}", payload.getReference());

        PaymentReference paymentRef = paymentReferenceRepository.findByReferenceCode(payload.getReference())
                .orElseThrow(() -> new WalletException("Payment reference not found: " + payload.getReference()));

        if (paymentRef.getStatus() == ReferenceStatus.COMPLETED) {
            throw new WalletException("This payment reference has already been processed.");
        }

        if (paymentRef.getAmount().compareTo(payload.getAmount()) != 0) {
            throw new WalletException("Amount mismatch. Expected: " + paymentRef.getAmount() + ", Received: " + payload.getAmount());
        }

        Wallet wallet = walletRepository.findByWalletId(String.valueOf(appUser.getId()))
                .orElseThrow(() -> new WalletException("Wallet not found for user ID: " + appUser.getId()));

        wallet.setBalance(wallet.getBalance().add(payload.getAmount()));
        walletRepository.updateUser(wallet);

        WalletLedgerEntry entry = new WalletLedgerEntry();
        entry.setWalletId(wallet.getWalletId());
        entry.setTransactionType(TransactionType.CREDIT);
        entry.setAmount(payload.getAmount());
        entry.setStatus(TransactionStatus.COMPLETED);
        entry.setDescription("Deposit via FCMB webhook");
        entry.setReference(payload.getReference());
        ledgerEntryRepository.save(entry);

        paymentRef.setStatus(ReferenceStatus.COMPLETED);
        paymentReferenceRepository.save(paymentRef);

        SuccessResponse response = new SuccessResponse();
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("Deposit processed successfully");
        response.setNewBalance(wallet.getBalance());

        return gson.toJson(response);
    }

    @Override
    @Transactional
    public void creditWalletByEmail(String email, BigDecimal amount, String reference) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));

        Wallet wallet = walletRepository.findByWalletId(String.valueOf(user.getId()))
                .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + user.getUserName()));

        if (ledgerEntryRepository.existsByReference(reference)) {
            log.warn("⛔ Duplicate transaction detected for reference: {}", reference);
            return; // Idempotency: prevent double processing
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.updateUser(wallet);

        WalletLedgerEntry entry = new WalletLedgerEntry();
        entry.setWalletId(wallet.getWalletId());
        entry.setTransactionType(TransactionType.CREDIT);
        entry.setAmount(amount);
        entry.setStatus(TransactionStatus.COMPLETED);
        entry.setDescription("Wallet funded via Paystack");
        entry.setReference(reference);
        ledgerEntryRepository.save(entry);

        auditLogService.logEvent(
                wallet.getWalletId(),
                user.getId().toString(),
                "CREDIT",
                reference,
                "Wallet funded via Paystack"
        );
    }

    @Override
    @Transactional
    public String confirmBankTransfer(ConfirmBankTransferRequest payload, String idToken, AppUser appUser) {
        Wallet wallet = walletRepository.findByAccountNumber(payload.getAccountNumber())
                .orElseThrow(() -> new WalletException("Wallet not found for account number: " + payload.getAccountNumber()));

        // ✅ Idempotency check
        if (ledgerEntryRepository.existsByReference(payload.getReference())) {
            SuccessResponse response = new SuccessResponse();
            response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
            response.setResponseMessage("Duplicate transfer already processed");
            response.setNewBalance(wallet.getBalance());
            return gson.toJson(response);
        }

        // ✅ Credit wallet
        wallet.setBalance(wallet.getBalance().add(payload.getAmount()));
        walletRepository.updateUser(wallet);

        // ✅ Ledger entry
        WalletLedgerEntry entry = new WalletLedgerEntry();
        entry.setWalletId(wallet.getWalletId());
        entry.setTransactionType(TransactionType.CREDIT);
        entry.setAmount(payload.getAmount());
        entry.setStatus(TransactionStatus.COMPLETED);
        entry.setDescription("Confirmed Bank Transfer");
        entry.setReference(payload.getReference());
        ledgerEntryRepository.save(entry);

        // ✅ Audit log
        auditLogService.logEvent(
                wallet.getWalletId(),
                appUser.getId().toString(),
                "CREDIT",
                payload.getReference(),
                "Confirmed Bank Transfer"
        );

        SuccessResponse response = new SuccessResponse();
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("Bank transfer confirmed and wallet credited successfully");
        response.setNewBalance(wallet.getBalance());
        return gson.toJson(response);
    }

}
