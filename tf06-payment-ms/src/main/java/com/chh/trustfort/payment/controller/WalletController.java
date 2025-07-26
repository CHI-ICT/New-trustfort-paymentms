package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Responses.ErrorResponse;
import com.chh.trustfort.payment.Responses.SuccessResponse;
import com.chh.trustfort.payment.Responses.WalletBalanceResponse;
import com.chh.trustfort.payment.Util.SecureResponseUtil;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.*;
import com.chh.trustfort.payment.enums.PaymentMethod;
import com.chh.trustfort.payment.enums.Role;
import com.chh.trustfort.payment.enums.TransactionStatus;
import com.chh.trustfort.payment.enums.TransactionType;
import com.chh.trustfort.payment.exception.WalletException;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.PendingBankTransfer;
import com.chh.trustfort.payment.model.Wallet;
import com.chh.trustfort.payment.model.WalletLedgerEntry;
import com.chh.trustfort.payment.payload.*;
import com.chh.trustfort.payment.repository.AppUserRepository;
import com.chh.trustfort.payment.repository.LedgerEntryRepository;
import com.chh.trustfort.payment.repository.PendingBankTransferRepository;
import com.chh.trustfort.payment.repository.WalletRepository;
import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.NotificationService;
import com.chh.trustfort.payment.service.WalletService;
import com.google.gson.Gson;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author dofoleta
 */
@RestController
@RefreshScope
@Tag(name = "Wallet API", description = "Handles wallet operations")
@RequiredArgsConstructor
public class WalletController {

    private static final Logger log = LoggerFactory.getLogger(WalletController.class);

    private final RequestManager requestManager;
    private final Gson gson;
    private final WalletService walletService;
    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final PendingBankTransferRepository pendingBankTransferRepository;
    private final AesService aesService;
    private final com.chh.trustfort.payment.service.AccountingClient accountingClient;
    private NotificationService notificationService;




    private static final String KEY_WITH_IV = "99A47258y83921B1627495826M729361/1234567890123456";

//    private UsersData extractUserFromToken(HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader == null || authHeader.isBlank()) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization header");
//        }
//
//        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
//
//        try {
//            String decrypted = aesService.decrypt(token, KEY_WITH_IV);
//            return gson.fromJson(decrypted, UsersData.class);
//        } catch (Exception e) {
//            log.error("Token decryption failed", e);
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
//        }
//    }

    private String buildEncryptedResponse(String responseCode, String responseMessage, Object data, AppUser appUser) {
        Map<String, Object> response = new HashMap<>();
        response.put("responseCode", responseCode);
        response.put("responseMessage", responseMessage);
        if (data != null) {
            response.put("data", data);
        }
        return aesService.encrypt(gson.toJson(response), appUser);
    }

    @GetMapping(value = ApiPath.GET_ALL_WALLETS_BY_USER_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getWalletsByPhoneNumber(
            @RequestParam String phoneNumber,
            @RequestHeader("Authorization") String idToken,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.GET_ALL_WALLETS_BY_USER_ID.getValue(),
                null,
                httpRequest,
                idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(
                    aesService.decrypt(request.payload, request.appUser),
                    OmniResponsePayload.class
            );

            return new ResponseEntity<>(
                    aesService.encrypt(SecureResponseUtil.error(
                            response.getResponseMessage(),
                            response.getResponseCode(),
                            "UNAUTHORIZED"
                    ), request.appUser),
                    HttpStatus.OK
            );
        }

        log.info("🔍 Fetching wallets using userId (phone number): {}", phoneNumber);
        String encryptedResponse = walletService.getWalletsByPhoneNumber(phoneNumber, request.appUser);
        return new ResponseEntity<>(encryptedResponse, HttpStatus.OK);
    }

    @PostMapping(value = ApiPath.CREATE_WALLET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createWallet(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        String idToken = authorizationHeader.replace("Bearer ", "").trim();
//        log.info("🔐 ID TOKEN: {}", idToken);
//        log.info("📥 RAW PAYLOAD (Base64): {}", requestPayload);

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.CREATE_WALLET.getValue(), requestPayload, httpRequest, idToken
        );
        // ✅ Set IP address manually since RequestManager doesn't do it
        request.appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            String decryptedError = aesService.decrypt(request.payload, request.appUser);
            OmniResponsePayload response = gson.fromJson(decryptedError, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

//        log.info("📥 Decrypted Payload: {}", request.payload);
        CreateWalletRequestPayload decryptedPayload = gson.fromJson(request.payload, CreateWalletRequestPayload.class);
        String result = walletService.createWallet(decryptedPayload, request.appUser);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = ApiPath.RECONCILE_BANK_TRANSFER, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> reconcileBankTransfer(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        String idToken = authorizationHeader.replace("Bearer ", "").trim();
//        log.info("🔐 ID TOKEN: {}", idToken);
//        log.info("📥 RAW PAYLOAD (Base64): {}", requestPayload);

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.RECONCILE_BANK_TRANSFER.getValue(), requestPayload, httpRequest, idToken
        );
        request.appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            String decryptedError = aesService.decrypt(request.payload, request.appUser);
            OmniResponsePayload response = gson.fromJson(decryptedError, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

//        log.info("📥 Decrypted Payload: {}", request.payload);
        BankTransferReconciliationRequest dto = gson.fromJson(request.payload, BankTransferReconciliationRequest.class);

        Optional<PendingBankTransfer> optional = pendingBankTransferRepository
                .findByReferenceAndStatus(dto.getReference(), "PENDING");

        if (optional.isEmpty()) {
            return new ResponseEntity<>(
                    aesService.encrypt(gson.toJson(new ErrorResponse("❌ Reference not found or already processed", "91")), request.appUser),
                    HttpStatus.OK
            );
        }

        PendingBankTransfer pending = optional.get();
        Wallet wallet = walletRepository.findByWalletId(pending.getWalletId())
                .orElseThrow(() -> new WalletException("❌ Wallet not found for ID: " + pending.getWalletId()));

        // 💳 Credit wallet
        wallet.setBalance(wallet.getBalance().add(pending.getAmount()));
        walletRepository.updateUser(wallet);

        // 🧾 Ledger entry
        WalletLedgerEntry ledgerEntry = WalletLedgerEntry.builder()
                .walletId(wallet.getWalletId())
                .transactionType(TransactionType.CREDIT)
                .amount(pending.getAmount())
                .status(TransactionStatus.COMPLETED)
                .description("Bank Transfer Wallet Funding")
                .reference(pending.getReference())
                .build();
        ledgerEntryRepository.save(ledgerEntry);

        String accountCode = wallet.getAccountCode() != null ? wallet.getAccountCode() : "WALLET-FUNDING";

        // 📒 Journal Entry to accounting
        JournalEntryRequest journal = new JournalEntryRequest();
        journal.setWalletId(wallet.getWalletId());
        journal.setAccountCode(accountCode);
        journal.setAmount(pending.getAmount());
        journal.setTransactionType("CREDIT");
        journal.setReference(pending.getReference());
        journal.setDescription("Bank Transfer Wallet Funding");
        journal.setTransactionDate(LocalDateTime.now());
        journal.setBusinessUnit("TRUSTFORT");
        journal.setDepartment("WALLET");

        try {
            String responses = accountingClient.postJournalEntryInternal(journal);  // ✅ Uses internal endpoint
            log.info("📘 Journal entry posted successfully for txRef {}: {}", responses);
        } catch (Exception je) {
            log.error("❌ Failed to post journal entry via Feign for txRef {}: {}", je.getMessage(), je);
        }


//        // ✉️ Notify user
//        notificationService.sendEmail(
//                wallet.getEmail(),
//                "Wallet Funded via Bank Transfer",
//                "Your wallet was credited with ₦" + pending.getAmount()
//        );

        // ✅ Mark transfer as completed
        pending.setStatus("COMPLETED");
        pending.setCompletedAt(LocalDateTime.now());
        pendingBankTransferRepository.save(pending);

        SuccessResponse success = new SuccessResponse();
        success.setResponseCode("00");
        success.setResponseMessage("✅ Wallet funded successfully via bank transfer");
        success.setNewBalance(wallet.getBalance());

        return new ResponseEntity<>(aesService.encrypt(gson.toJson(success), request.appUser), HttpStatus.OK);
    }


    @PostMapping(value = ApiPath.FUND_WALLET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> fundWallet(
            @RequestHeader String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        log.info("📥 Encrypted payload received for wallet funding");

        // 🔐 Step 1: Decrypt & authorize user
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.FUND_WALLET.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError || request.appUser == null) {
            log.warn("❌ Wallet funding auth failed: {}", request.payload);
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(aesService.encrypt(gson.toJson(error), new AppUser()));
        }

        // ✅ Step 2: Extract decrypted payload and AppUser
        AppUser appUser = request.appUser;
        FundWalletRequestPayload payload = gson.fromJson(request.payload, FundWalletRequestPayload.class);

        // ✅ Extract sender's phone number from idToken and patch into AppUser
        String decryptedIdToken = aesService.decrypt(idToken, appUser);
        JsonObject tokenJson = JsonParser.parseString(decryptedIdToken).getAsJsonObject();
        String phoneNumber = tokenJson.getAsJsonObject("data").get("phoneNumber").getAsString();
        appUser.setPhoneNumber(phoneNumber);  // 🔥 This line fixes the crash

        String encryptedResponse = walletService.initiateWalletFunding(payload, appUser);
        return ResponseEntity.ok(encryptedResponse);
    }

    @GetMapping(value = ApiPath.PAYMENT_OPTIONS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPaymentOptions(
            @RequestHeader("Authorization") String idToken,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.FUND_WALLET.getValue(), // Or Role.FUND_WALLET.getValue()
                null,
                httpRequest,
                idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(
                    aesService.decrypt(request.payload, request.appUser),
                    OmniResponsePayload.class
            );

            return ResponseEntity.ok(
                    aesService.encrypt(SecureResponseUtil.error(
                            response.getResponseMessage(),
                            response.getResponseCode(),
                            "UNAUTHORIZED"
                    ), request.appUser)
            );
        }

        log.info("📋 Fetching list of available payment options...");

        // Build payment method list
        List<PaymentOptionDTO> options = Arrays.stream(PaymentMethod.values())
                .map(method -> new PaymentOptionDTO(method.name(), getDescription(method)))
                .collect(Collectors.toList());

        // Standard response map
        Map<String, Object> response = new HashMap<>();
        response.put("responseCode", "00");
        response.put("responseMessage", "Available payment methods");
        response.put("data", options);

        String encryptedResponse = aesService.encrypt(gson.toJson(response), request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }

    private String getDescription(PaymentMethod method) {
        switch (method) {
            case WALLET:
                return "Pay with Wallet Balance";
            case PAYSTACK:
                return "Pay with Card (via Paystack)";
            case FLUTTERWAVE:
                return "Pay with Card (via Flutterwave)";
            case BANK_TRANSFER:
                return "Pay via Bank Transfer";
            case OPEN_BANKING:
                return "Pay via Open Banking";
            default:
                return "Unknown Payment Method";
        }

}


//    @GetMapping(value = ApiPath.FETCH_ALL_WALLETS, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Object> fetchAllWallets(HttpServletRequest httpRequest) {
//
//        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
//                Role.FETCH_WALLET.getValue(),
//                "", // No payload needed
//                httpRequest,
//                ApiPath.ID_TOKEN
//        );
//
//        if (request.isError) {
//            return new ResponseEntity<>(request.payload, HttpStatus.OK);
//        }
//
//        AppUser user = request.appUser;
//
//        String encryptedResponse = walletService.fetchAllWallets(String.valueOf(user.getId()), user);
//        return new ResponseEntity<>(encryptedResponse, HttpStatus.OK);
//    }


    @PostMapping(value = ApiPath.TRANSFER_FUNDS, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> transferFunds(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        String idToken = authorizationHeader.replace("Bearer ", "").trim();
//        log.info("🔐 ID TOKEN: {}", idToken);
//        log.info("📥 RAW PAYLOAD (Base64): {}", requestPayload);

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.TRANSFER_FUNDS.getValue(), requestPayload, httpRequest, idToken
        );

        // Set IP address for traceability
        if (request.appUser != null) {
            request.appUser.setIpAddress(httpRequest.getRemoteAddr());
        }

        if (request.isError) {
            String decryptedError = aesService.decrypt(request.payload, request.appUser);
            OmniResponsePayload error = gson.fromJson(decryptedError, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(error.getResponseCode(), error.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        log.info("📥 Decrypted Payload: {}", request.payload);
        FundsTransferRequestPayload transferPayload = gson.fromJson(request.payload, FundsTransferRequestPayload.class);

        String encryptedResponse = walletService.transferFunds(
                transferPayload,
                idToken,
                request.appUser,
                request.appUser
        );

        return ResponseEntity.ok(encryptedResponse);
    }


    @GetMapping(value = ApiPath.CHECK_BALANCE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkWalletBalanceByPhoneNumber(
            @RequestParam String phoneNumber,
            @RequestHeader("Authorization") String idToken,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.CHECK_BALANCE.getValue(),
                null,
                httpRequest,
                idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(
                    aesService.decrypt(request.payload, request.appUser),
                    OmniResponsePayload.class
            );

            return ResponseEntity.ok(
                    aesService.encrypt(SecureResponseUtil.error(
                            response.getResponseMessage(),
                            response.getResponseCode(),
                            "UNAUTHORIZED"
                    ), request.appUser)
            );
        }

        log.info("📦 Checking wallet balance for phoneNumber: {}", phoneNumber);
        String encryptedResponse = walletService.checkBalanceByPhoneNumber(phoneNumber, request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }

//    @PostMapping(value = ApiPath.INTERNAL_CHECK_BALANCE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> internalCheckWalletBalance(
//            @RequestParam String phoneNumber
//    ) {
//        try {
//            // ✅ Get wallet entity using phoneNumber
//            List<Wallet> wallets = walletRepository.findByUserId(phoneNumber);
//            WalletBalanceResponse response = new WalletBalanceResponse();
//
//            if (wallets.isEmpty()) {
//                log.warn("❌ No wallet found for phoneNumber: {}", phoneNumber);
//                response.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());
//                response.setMessage("No wallet found for this user");
//            } else {
//                Wallet wallet = wallets.get(0); // first match
//                if (wallet.getStatus() == WalletStatus.SUSPENDED) {
//                    response.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());
//                    response.setMessage("Wallet is frozen and cannot be accessed");
//                } else if (wallet.getStatus() == WalletStatus.CLOSED) {
//                    response.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());
//                    response.setMessage("Wallet is closed and cannot be accessed");
//                } else {
//                    response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//                    response.setMessage("Success");
//                    response.setWalletId(wallet.getWalletId());
//                    response.setBalance(wallet.getBalance());
//                    response.setLedgerBalance(wallet.getLedgerBalance());
//                }
//            }
//
//            // ✅ Encrypt response with internal system user
//            AppUser internalUser = appUserRepository.getAppUserByUserName("TRUSTFORT001");
//            if (internalUser == null) {
//                internalUser = new AppUser(); // fallback dummy encryption key
//                internalUser.setEncryptionKey("dummy-key-for-encryption");
//            }
//
//            String encrypted = aesService.encrypt(gson.toJson(response), internalUser);
//            return ResponseEntity.ok(encrypted);
//
//        } catch (Exception ex) {
//            log.error("❌ Error checking wallet balance internally", ex);
//
//            WalletBalanceResponse error = new WalletBalanceResponse();
//            error.setResponseCode("96");
//            error.setMessage("Internal error: unable to retrieve wallet balance");
//
//            AppUser fallback = new AppUser();
//            fallback.setEncryptionKey("dummy-key-for-encryption");
//
//            return ResponseEntity.ok(aesService.encrypt(gson.toJson(error), fallback));
//        }
//    }

        @GetMapping(value = ApiPath.INTERNAL_CHECK_BALANCE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> internalCheckWalletBalance(@RequestParam String phoneNumber) {
        log.info("📡 Internal call to check wallet balance for phone: {}", phoneNumber);

        try {
            WalletBalanceResponse response = walletService.checkBalancePlain(phoneNumber);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error in internal wallet balance check for {}: {}", phoneNumber, e.getMessage(), e);

            WalletBalanceResponse error = new WalletBalanceResponse();
            error.setResponseCode("96");
            error.setMessage("Unable to retrieve wallet balance");

            return ResponseEntity.ok(error);
        }
    }

//    @GetMapping(value = ApiPath.TRANSACTION_HISTORY, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> processTransactionHistoryRequest(
//            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
//            @RequestParam("phoneNumber") String phoneNumber,
//            @RequestHeader("Authorization") String jwtToken,
//            HttpServletRequest httpRequest) {
//
//        // ✅ Validate using JWT token (no idToken or payload involved)
//        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
//                Role.TRANSACTION_HISTORY.getValue(), null, httpRequest, jwtToken
//        );
//
//        if (request.isError || request.appUser == null) {
//            return ResponseEntity.ok(aesService.encrypt(gson.toJson(
//                    new ErrorResponse("06", "Unauthorized request")
//            ), null));
//        }
//
//        AppUser appUser = request.appUser;
//
//        log.info("🔍 PhoneNumber passed in request param: {}", phoneNumber);
//
//        // 🔍 Find wallet by phone number (stored as userId)
//        Optional<Wallet> walletOpt = walletRepository.findByUserId(phoneNumber).stream().findFirst();
//
//        if (!walletOpt.isPresent()) {
//            log.warn("❌ No wallet found for phoneNumber: {}", phoneNumber);
//            return ResponseEntity.ok(aesService.encrypt(gson.toJson(
//                    new ErrorResponse("06", "Wallet not found for user")
//            ), appUser));
//        }
//
//        String walletId = walletOpt.get().getWalletId();
//
//        try {
//            List<LedgerEntry> transactions = walletService.getTransactionHistory(walletId, startDate, endDate, phoneNumber).getBody();
//            return ResponseEntity.ok(aesService.encrypt(gson.toJson(transactions), appUser));
//        } catch (WalletException e) {
//            return ResponseEntity.ok(aesService.encrypt(gson.toJson(
//                    new ErrorResponse("06", e.getMessage())
//            ), appUser));
//        } catch (Exception e) {
//            log.error("❌ Error retrieving transaction history: {}", e.getMessage(), e);
//            return ResponseEntity.ok(aesService.encrypt(gson.toJson(
//                    new ErrorResponse("06", "Internal server error")
//            ), appUser));
//        }
//    }
@GetMapping(value = ApiPath.TRANSACTION_HISTORY, produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<String> processTransactionHistoryRequest(
        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam("phoneNumber") String phoneNumber,
        @RequestParam(value = "transactionType", required = false) TransactionType transactionType,
        @RequestParam(value = "status", required = false) TransactionStatus status,
        @RequestParam(value = "transactionReference", required = false) String transactionReference,
        @RequestParam(value = "sessionId", required = false) String sessionId,
        @RequestHeader("Authorization") String jwtToken,
        HttpServletRequest httpRequest
) {
    Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
            Role.TRANSACTION_HISTORY.getValue(), null, httpRequest, jwtToken
    );

    if (request.isError || request.appUser == null) {
        return ResponseEntity.ok(buildEncryptedResponse("06", "Unauthorized request", null, new AppUser()));
    }

    AppUser appUser = request.appUser;
    log.info("🔍 Fetching transactions for phoneNumber: {}", phoneNumber);

    Optional<Wallet> walletOpt = walletRepository.findByUserId(phoneNumber).stream().findFirst();
    if (!walletOpt.isPresent()) {
        return ResponseEntity.ok(buildEncryptedResponse("06", "Wallet not found", null, appUser));
    }

    String walletId = walletOpt.get().getWalletId();

    try {
        return walletService.getTransactionHistory(walletId, startDate, endDate, phoneNumber, transactionType, status,transactionReference, sessionId, appUser);
    } catch (WalletException e) {
        return ResponseEntity.ok(buildEncryptedResponse("06", e.getMessage(), null, appUser));
    } catch (Exception e) {
        log.error("❌ Error retrieving transaction history", e);
        return ResponseEntity.ok(buildEncryptedResponse("06", "Internal server error", null, appUser));
    }
}


//    @PostMapping(value = "/withdraw", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> withdrawFunds(@RequestBody String payload, HttpServletRequest httpRequest, @RequestHeader("idToken") String idToken) {
//        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
//                Role.WITHDRAW_FUNDS.getValue(), payload, httpRequest, idToken
//        );
//
//        if (request.isError) {
//            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
//            return ResponseEntity.ok(SecureResponseUtil.error(error.getResponseCode(), error.getResponseMessage(), "400"));
//        }
//
//        WithdrawFundsRequestPayload withdrawPayload = gson.fromJson(request.payload, WithdrawFundsRequestPayload.class);
//
//        String encryptedResponse = walletService.withdrawFunds(
//                withdrawPayload,
//                String.valueOf(request.appUser.getId()),
//                request.appUser.getEmail(),
//                idToken,
//                request.appUser
//        );
//
//        return ResponseEntity.ok(encryptedResponse);
//    }


    @PostMapping(value = ApiPath.PURCHASE_WITH_WALLET, consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> purchaseProductFromWallet(@RequestHeader String idToken,
                                                       @RequestBody String requestPayload,
                                                       HttpServletRequest request) {
        Quintuple<Boolean, String, String, AppUser, String> validated = requestManager.validateRequest(
                Role.FUND_WALLET.getValue(), null, request, idToken);

        if (validated.isError || validated.appUser == null) {
            return ResponseEntity.ok(aesService.encrypt(gson.toJson(new ErrorResponse("Unauthorized", "06")), null));
        }

        AppUser appUser = validated.appUser;
        ProductPurchaseDTO dto = gson.fromJson(aesService.decrypt(requestPayload, appUser), ProductPurchaseDTO.class);
        String encryptedResponse = walletService.deductWalletForProductPurchase(dto, appUser, appUser);
        return ResponseEntity.ok(encryptedResponse);
    }


    @PostMapping(value = ApiPath.FREEZE_WALLET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> freezeWallet(
            @RequestHeader("Authorization") String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

    Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
            Role.FREEZE_WALLET.getValue(),
            requestPayload,
            httpRequest,
            idToken
    );

    if (request.isError) {
        OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
        return new ResponseEntity<>(
                SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                HttpStatus.OK
        );
    }

    String serviceResponse = walletService.freezeWallet(request.payload, idToken, request.appUser);
    return new ResponseEntity<>(aesService.encrypt(serviceResponse, request.appUser), HttpStatus.OK);
}


    @PostMapping(value = ApiPath.UNFREEZE_WALLET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processUnfreezeWalletRequest(
            @RequestHeader("Authorization") String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.UNFREEZE_WALLET.getValue(), requestPayload, httpRequest, idToken);

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(SecureResponseUtil.error(
                    response.getResponseCode(),
                    response.getResponseMessage(),
                    String.valueOf(HttpStatus.BAD_REQUEST)), HttpStatus.OK);
        }

        UnfreezeWalletRequestPayload payload = gson.fromJson(request.payload, UnfreezeWalletRequestPayload.class);
        String serviceResponse = walletService.unfreezeWallet(payload, idToken, request.appUser);

        return new ResponseEntity<>(aesService.encrypt(serviceResponse, request.appUser), HttpStatus.OK);
    }


    @PostMapping(value = ApiPath.CLOSE_WALLET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processCloseWalletRequest(
            @RequestHeader("Authorization") String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.CLOSE_WALLET.getValue(), requestPayload, httpRequest, idToken);

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(SecureResponseUtil.error(
                    response.getResponseCode(),
                    response.getResponseMessage(),
                    String.valueOf(HttpStatus.BAD_REQUEST)), HttpStatus.OK);
        }

        CloseWalletRequestPayload payload = gson.fromJson(request.payload, CloseWalletRequestPayload.class);
        String serviceResponse = walletService.closeWallet(payload, idToken, request.appUser);

        return new ResponseEntity<>(aesService.encrypt(serviceResponse, request.appUser), HttpStatus.OK);
    }


    @PostMapping(value = ApiPath.LOCK_FUNDS, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processLockFundsRequest(
            @RequestHeader("Authorization") String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.LOCK_FUNDS.getValue(), requestPayload, httpRequest, idToken);

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(SecureResponseUtil.error(
                    response.getResponseCode(),
                    response.getResponseMessage(),
                    String.valueOf(HttpStatus.BAD_REQUEST)), HttpStatus.OK);
        }

        LockFundsRequestPayload payload = gson.fromJson(request.payload, LockFundsRequestPayload.class);
        String serviceResponse = walletService.lockFunds(payload, idToken, request.appUser);

        return new ResponseEntity<>(aesService.encrypt(serviceResponse, request.appUser), HttpStatus.OK);
    }


    @PostMapping(value = ApiPath.UNLOCK_FUNDS, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processUnlockFundsRequest(
            @RequestHeader("Authorization") String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.UNLOCK_FUNDS.getValue(), requestPayload, httpRequest, idToken);

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(SecureResponseUtil.error(
                    response.getResponseCode(),
                    response.getResponseMessage(),
                    String.valueOf(HttpStatus.BAD_REQUEST)), HttpStatus.OK);
        }

        UnlockFundsRequestPayload payload = gson.fromJson(request.payload, UnlockFundsRequestPayload.class);
        String serviceResponse = walletService.unlockFunds(payload, idToken, request.appUser);

        return new ResponseEntity<>(aesService.encrypt(serviceResponse, request.appUser), HttpStatus.OK);
    }


    @PostMapping(value = ApiPath.UPDATE_WALLET_BALANCE, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processUpdateWalletBalanceRequest(
            @RequestHeader("Authorization") String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.UPDATE_WALLET_BALANCE.getValue(), requestPayload, httpRequest, idToken);

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(SecureResponseUtil.error(
                    response.getResponseCode(),
                    response.getResponseMessage(),
                    String.valueOf(HttpStatus.BAD_REQUEST)), HttpStatus.OK);
        }

        UpdateWalletBalancePayload payload = gson.fromJson(request.payload, UpdateWalletBalancePayload.class);
        String response = walletService.updateWalletBalance(payload, idToken, request.appUser);

        return new ResponseEntity<>(aesService.encrypt(response, request.appUser), HttpStatus.OK);
    }

    @PostMapping(value = ApiPath.FUND_WEBHOOK, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processWebhookFromFcmb(@RequestParam String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.PROCESS_WEBHOOK.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        FcmbWebhookPayload decryptedPayload = gson.fromJson(request.payload, FcmbWebhookPayload.class);
        String result = (String) walletService.processWebhookDeposit(decryptedPayload, idToken, request.appUser);
        return new ResponseEntity<>(aesService.encrypt(result, request.appUser), HttpStatus.OK);
    }

    @PostMapping(value = ApiPath.CREDIT_WALLET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> creditWalletByEmail(@RequestParam String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {

        // 🔐 Step 1: Validate and decrypt the request
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.CREDIT_WALLET.getValue(), requestPayload, httpRequest, idToken
        );

        // ❌ Step 2: Handle validation failure
        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        // ✅ Step 3: Parse decrypted payload into the expected DTO
        CreditWalletRequestPayload decryptedPayload = gson.fromJson(request.payload, CreditWalletRequestPayload.class);

        // ⚙️ Step 4: Invoke service with raw parameters
        walletService.creditWalletByEmail(
                decryptedPayload.getEmail(),
                decryptedPayload.getAmount(),
                decryptedPayload.getReference()
        );

        // 🔒 Step 5: Return encrypted success response
        String responseMessage = "Wallet credited successfully";
        return new ResponseEntity<>(aesService.encrypt(responseMessage, request.appUser), HttpStatus.OK);
    }




}
