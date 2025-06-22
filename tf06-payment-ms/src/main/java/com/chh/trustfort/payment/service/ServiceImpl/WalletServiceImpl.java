package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.Responses.*;
import com.chh.trustfort.payment.component.AccountingClient;
import com.chh.trustfort.payment.component.ResponseCode;
import com.chh.trustfort.payment.component.WalletUtil;
import com.chh.trustfort.payment.dto.ConfirmBankTransferRequest;
import com.chh.trustfort.payment.dto.JournalEntryRequest;
import com.chh.trustfort.payment.dto.WalletDTO;
import com.chh.trustfort.payment.enums.ReferenceStatus;
import com.chh.trustfort.payment.enums.TransactionStatus;
import com.chh.trustfort.payment.enums.TransactionType;
import com.chh.trustfort.payment.enums.WalletStatus;
import com.chh.trustfort.payment.exception.WalletException;
import com.chh.trustfort.payment.model.*;
import com.chh.trustfort.payment.payload.*;
import com.chh.trustfort.payment.repository.*;
import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.*;
import com.google.gson.Gson;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
    private final UsersService usersService;
    private final OtpService otpService;
    private final WalletUtil walletUtil;
    private final AuditLogService auditLogService;
    private final AesService aesService;
    private final PinService pinService;
    private final UsersRepository usersRepository;
    private final AccountingClient accountingClient;
    private final NotificationService notificationService;
    private final MessageSource messageSource;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final SettlementAccountRepository settlementAccountRepository;
    private final FraudDetectionService fraudDetectionService;
    private final MockFCMBIntegrationService fcmbIntegrationService;
    private final PaymentReferenceRepository paymentReferenceRepository;
    private final SystemParameterService systemParameterService;
    private final PaystackTransferService paystackTransferService;
    private final Gson gson;
    @Autowired
    private HttpServletRequest httpServletRequest;

    private Wallet getWalletOrThrow(String walletId) {
        return walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new WalletException("Wallet not found for ID: " + walletId));
    }

    @Override
    public List<WalletDTO> getWalletsByUserId(String userId) {
        log.info("🔎 Querying wallets for userId: {}", userId);
        List<Wallet> wallets = walletRepository.findByUserId(userId);

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
            List<Wallet> wallets = walletRepository.findByUserId(phoneNumber); // ✅ Already using phoneNumber

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

            response.setResponseCode("00");
            response.setResponseMessage("Wallets retrieved successfully");
            response.setWallets(walletDTOs);
        } catch (Exception ex) {
            log.error("❌ Error fetching wallets by phone number: {}", ex.getMessage(), ex);
            response.setResponseMessage("An unexpected error occurred");
        }

        return aesService.encrypt(gson.toJson(response), appUser);
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
            log.warn("Phone Number or Email Address is null");
            response.setResponseMessage(messageSource.getMessage("user.not.found", null, Locale.ENGLISH));
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        String phoneNumber = userData.getPhoneNumber();
        if (walletRepository.existsByUserId(phoneNumber)) {
            log.warn("Wallet already exists for phone number: {}", phoneNumber);
            response.setResponseMessage(messageSource.getMessage("wallet.already.exists", null, Locale.ENGLISH));
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        String walletId = walletRepository.generateWalletId(); // This is just an internal unique ID
        String serialNumber = walletId.replace("WAL-", "");
        String accountNumber = walletUtil.generateAccountNumber(); // ✅ This must generate a proper 10/12-digit number
        String currency = (requestPayload.getCurrency() != null) ? requestPayload.getCurrency() : "NGN";

        Users users = usersRepository.findById(Long.valueOf(userData.getUserId()))
                .orElse(null);

        if (users == null) {
            log.warn("❌ No Users entity found for DB userId: {}", userData.getUserId());
            response.setResponseMessage(messageSource.getMessage("user.not.found", null, Locale.ENGLISH));
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        Wallet wallet = new Wallet();
        wallet.setWalletId(walletId);                          // ✅ WAL-100001
        wallet.setSerialNumber(Long.parseLong(serialNumber));  // ✅ e.g. 100001
        wallet.setUserId(phoneNumber);                         // ✅ Use phone number as user identifier
        wallet.setEmail(userData.getEmailAddress());
        wallet.setCurrency(currency);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatus(WalletStatus.ACTIVE);
        wallet.setPhoneNumber(phoneNumber);
        wallet.setAccountNumber(accountNumber);                // ✅ Save correct account number
        wallet.setUsers(users);

        wallet = walletRepository.createWallet(wallet);
        log.info("✅ Wallet created successfully: {}", wallet.getWalletId());

        if (walletUtil.validateWalletId(wallet.getWalletId())) {
            response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
            response.setResponseMessage(messageSource.getMessage("wallet.created.success", null, Locale.ENGLISH));
            response.setWalletId(wallet.getWalletId());

            AppUserActivity activity = new AppUserActivity();
            activity.setAppUser(appUser);
            activity.setActivity("CREATE_WALLET");
            activity.setDescription("Wallet created successfully with ID: " + wallet.getWalletId());
            activity.setIpAddress(appUser.getIpAddress());
            activity.setCreatedBy(userData.getUserName());
            activity.setCreatedAt(LocalDateTime.now());
            activity.setStatus('S');

            appUserRepository.createUserActivity(activity);
        }

        return aesService.encrypt(gson.toJson(response), appUser);
    }




    @Override
    @Transactional
    public String fundWallet(FundWalletRequestPayload payload, String userId, String emailAddress) {
        log.info("Funding wallet for user ID: {}", userId);

        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
                .orElseThrow(() -> new WalletException("Wallet not found for ID: " + payload.getWalletId()));

        if (wallet.getStatus() == WalletStatus.SUSPENDED || wallet.getStatus() == WalletStatus.CLOSED) {
            return gson.toJson(new ErrorResponse("Wallet is not active", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        if (!wallet.getUserId().equals(userId)) {
            return gson.toJson(new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        BigDecimal creditAmount = payload.getAmount();
        wallet.setBalance(wallet.getBalance().add(creditAmount));
        walletRepository.updateUser(wallet);

        LedgerEntry ledgerEntry = new LedgerEntry();
        ledgerEntry.setWalletId(wallet.getWalletId());
        ledgerEntry.setTransactionType(TransactionType.CREDIT);
        ledgerEntry.setAmount(creditAmount);
        ledgerEntry.setStatus(TransactionStatus.COMPLETED);
        ledgerEntry.setDescription("Wallet Funding");
        ledgerEntryRepository.save(ledgerEntry);

        JournalEntryRequest journal = new JournalEntryRequest();
        journal.setAccountCode("REV001");
        journal.setAmount(creditAmount);
        journal.setDescription("Wallet Funding");
        journal.setTransactionType("CREDIT");
        journal.setDepartment("Wallet");
        journal.setBusinessUnit("Retail");
        journal.setTransactionDate(LocalDateTime.now());

        accountingClient.postJournalEntry(journal);

        SuccessResponse response = new SuccessResponse();
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("Wallet funded successfully");
        response.setNewBalance(wallet.getBalance());

        notificationService.sendEmail(emailAddress,
                "🔺 Wallet Funded",
                "Your wallet was credited with ₦" + creditAmount + " via manual funding.");

        log.info("Wallet ID: {} funded successfully. New balance: {}", wallet.getWalletId(), wallet.getBalance());

        return gson.toJson(response);
    }
    @Override
    public String fetchWallet(String walletId, String userId, AppUser user) {
        Wallet wallet = walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new WalletException("Wallet not found for ID: " + walletId));

        if (!wallet.getUserId().equals(userId)) {
            ErrorResponse errorResponse = new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode());
            return aesService.encrypt(gson.toJson(errorResponse), user);  // ✅ pass AppUser
        }

        WalletResponse walletResponse = new WalletResponse(
                ResponseCode.SUCCESS.getResponseCode(),
                "Wallet retrieved successfully",
                wallet
        );

        return aesService.encrypt(gson.toJson(walletResponse), user); // ✅ pass AppUser
    }



    @Override
    @Transactional
    public String transferFunds(FundsTransferRequestPayload payload, String idToken, AppUser appUser, AppUser ecred) {
        // ✅ Fetch sender wallet
        Wallet senderWallet = walletRepository.findByWalletId(payload.getSenderWalletId())
                .orElseThrow(() -> new WalletException("Sender wallet not found: " + payload.getSenderWalletId()));

        if (!senderWallet.getUserId().equals(appUser.getId())) {
            return aesService.encrypt(gson.toJson(
                    new ErrorResponse("Unauthorized access to sender wallet", ResponseCode.FAILED_TRANSACTION.getResponseCode())
            ), ecred);
        }

        // ✅ Fetch receiver wallet
        Wallet receiverWallet = walletRepository.findByWalletId(payload.getReceiverWalletId())
                .orElseThrow(() -> new WalletException("Receiver wallet not found: " + payload.getReceiverWalletId()));

        BigDecimal transferAmount = payload.getAmount();
        if (senderWallet.getBalance().compareTo(transferAmount) < 0) {
            return aesService.encrypt(gson.toJson(
                    new ErrorResponse("Insufficient funds in sender wallet", ResponseCode.FAILED_TRANSACTION.getResponseCode())
            ), ecred);
        }

        // ✅ Perform debit from sender
        UpdateWalletBalancePayload debitPayload = new UpdateWalletBalancePayload();
        debitPayload.setWalletId(senderWallet.getWalletId());
        debitPayload.setAmount(transferAmount.negate().doubleValue());

        String debitResultJson = updateWalletBalance(debitPayload, idToken, appUser);
        JsonObject debitResult = gson.fromJson(debitResultJson, JsonObject.class);
        if (!debitResult.get("responseCode").getAsString().equals(ResponseCode.SUCCESS.getResponseCode())) {
            return aesService.encrypt(debitResultJson, ecred);
        }

        // ✅ Perform credit to receiver
        UpdateWalletBalancePayload creditPayload = new UpdateWalletBalancePayload();
        creditPayload.setWalletId(receiverWallet.getWalletId());
        creditPayload.setAmount(transferAmount.doubleValue());

        String creditResultJson = updateWalletBalance(creditPayload, idToken, appUser);
        JsonObject creditResult = gson.fromJson(creditResultJson, JsonObject.class);
        if (!creditResult.get("responseCode").getAsString().equals(ResponseCode.SUCCESS.getResponseCode())) {
            return aesService.encrypt(creditResultJson, ecred);
        }

        // ✅ Ledger entries
        LedgerEntry senderEntry = new LedgerEntry();
        senderEntry.setWalletId(senderWallet.getWalletId());
        senderEntry.setTransactionType(TransactionType.DEBIT);
        senderEntry.setAmount(transferAmount);
        senderEntry.setStatus(TransactionStatus.COMPLETED);
        senderEntry.setDescription("Transfer to " + receiverWallet.getWalletId());
        ledgerEntryRepository.save(senderEntry);

        LedgerEntry receiverEntry = new LedgerEntry();
        receiverEntry.setWalletId(receiverWallet.getWalletId());
        receiverEntry.setTransactionType(TransactionType.CREDIT);
        receiverEntry.setAmount(transferAmount);
        receiverEntry.setStatus(TransactionStatus.COMPLETED);
        receiverEntry.setDescription("Transfer from " + senderWallet.getWalletId());
        ledgerEntryRepository.save(receiverEntry);

        // ✅ Journal entries
        LocalDateTime now = LocalDateTime.now();

        JournalEntryRequest debitEntry = new JournalEntryRequest();
        debitEntry.setAccountCode("WALLET001");
        debitEntry.setAmount(transferAmount);
        debitEntry.setDescription("Transfer to " + receiverWallet.getWalletId());
        debitEntry.setTransactionType("DEBIT");
        debitEntry.setDepartment("Wallet");
        debitEntry.setBusinessUnit("Retail");
        debitEntry.setTransactionDate(now);
        accountingClient.postJournalEntry(debitEntry);

        JournalEntryRequest creditEntry = new JournalEntryRequest();
        creditEntry.setAccountCode("WALLET001");
        creditEntry.setAmount(transferAmount);
        creditEntry.setDescription("Transfer from " + senderWallet.getWalletId());
        creditEntry.setTransactionType("CREDIT");
        creditEntry.setDepartment("Wallet");
        creditEntry.setBusinessUnit("Retail");
        creditEntry.setTransactionDate(now);
        accountingClient.postJournalEntry(creditEntry);

        // ✅ Notifications
        notificationService.sendEmail(
                appUser.getEmail(),
                "🔻 Wallet Debit - Transfer",
                "You transferred ₦" + transferAmount + " to " + receiverWallet.getWalletId()
        );

        notificationService.sendEmail(
                receiverWallet.getEmail(),
                "🔺 Wallet Credit - Incoming Transfer",
                "You received ₦" + transferAmount + " from " + senderWallet.getWalletId()
        );

        // ✅ Success response
        SuccessResponse response = new SuccessResponse();
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("Transfer processed successfully");
        response.setNewBalance(debitResult.get("newBalance").getAsBigDecimal());

        return aesService.encrypt(gson.toJson(response), ecred);
    }

    @Override
    public WalletBalanceResponse getWalletBalance(String walletId, String userId) {
        log.info("Fetching balance for Wallet ID: {}", walletId);

        WalletBalanceResponse response = new WalletBalanceResponse();
        response.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());

        Wallet wallet = getWalletOrThrow(walletId);

        if (wallet.getStatus() == WalletStatus.SUSPENDED) {
            log.warn("Wallet is frozen: {}", walletId);
            response.setMessage("Wallet is frozen and cannot be accessed");
            return response;
        }

        if (!wallet.getUserId().equals(userId)) {
            log.warn("Unauthorized access to wallet: {}", walletId);
            response.setMessage("Unauthorized access to this wallet");
            return response;
        }

        if (wallet.getStatus() == WalletStatus.CLOSED) {
            response.setMessage("Wallet is closed and cannot be accessed");
            return response;
        }

        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setMessage("Balance retrieved successfully");
        response.setWalletId(walletId);
        response.setBalance(wallet.getBalance());

        return response;
    }

    @Override
    public ResponseEntity<List<LedgerEntry>> getTransactionHistory(String walletId, LocalDate startDate, LocalDate endDate, String userId) {
        Wallet wallet = walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new WalletException("Wallet not found for ID: " + walletId));

        if (!wallet.getUserId().equals(userId)) {
            log.warn("Unauthorized access to transaction history for wallet ID: {}", walletId);
            throw new WalletException("Unauthorized access to this wallet");
        }

        List<LedgerEntry> transactions = ledgerEntryRepository.findByWalletId(walletId);
        return (ResponseEntity<List<LedgerEntry>>) transactions;
    }

    @Override
    @Transactional
    public String withdrawFunds(WithdrawFundsRequestPayload payload, String userId, String email, String idToken, AppUser appUser) {
        log.info("Processing withdrawal for wallet ID: {}", payload.getWalletId());

        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new WalletException("User not found: " + email));

        if (!otpService.validateOtp(Long.valueOf(user.getId()), String.valueOf(payload.getOtpCode()), "WITHDRAW_FUNDS")) {
            log.warn("Invalid or expired OTP for user: {}", user.getUserName());
            return aesService.encrypt(gson.toJson(new ErrorResponse("Invalid or expired OTP",
                    ResponseCode.FAILED_TRANSACTION.getResponseCode())), appUser);
        }

        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
                .orElseThrow(() -> new WalletException("Wallet not found for ID: " + payload.getWalletId()));

        if (!wallet.getUserId().equals(userId)) {
            return aesService.encrypt(gson.toJson(new ErrorResponse("Unauthorized access",
                    ResponseCode.FAILED_TRANSACTION.getResponseCode())), appUser);
        }

        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            return aesService.encrypt(gson.toJson(new ErrorResponse("Withdrawal not allowed. Wallet is " + wallet.getStatus(),
                    ResponseCode.FAILED_TRANSACTION.getResponseCode())), appUser);
        }

        if (fraudDetectionService.isFraudulentWithdrawal(user, payload.getAmount())) {
            return aesService.encrypt(gson.toJson(new ErrorResponse("Suspicious transaction: Limit exceeded",
                    ResponseCode.FAILED_TRANSACTION.getResponseCode())), appUser);
        }

        BigDecimal withdrawalAmount = payload.getAmount();
        if (wallet.getBalance().compareTo(withdrawalAmount) < 0) {
            return aesService.encrypt(gson.toJson(new ErrorResponse("Insufficient funds",
                    ResponseCode.FAILED_TRANSACTION.getResponseCode())), appUser);
        }

        // System Parameters
        String debitAccount = systemParameterService.getValue("WALLET_WITHDRAWAL_DEBIT_ACCOUNT");
        String creditAccount = systemParameterService.getValue("WALLET_WITHDRAWAL_CREDIT_ACCOUNT");
        String department = systemParameterService.getValue("WALLET_DEPARTMENT");
        String businessUnit = systemParameterService.getValue("WALLET_BUSINESS_UNIT");
        String settlementAccountNo = systemParameterService.getValue("WALLET_SETTLEMENT_ACCOUNT");

        wallet.setBalance(wallet.getBalance().subtract(withdrawalAmount));
        walletRepository.updateUser(wallet);

        // Journal Entries
        JournalEntryRequest debitEntry = new JournalEntryRequest();
        debitEntry.setAccountCode(debitAccount);
        debitEntry.setAmount(withdrawalAmount);
        debitEntry.setDescription("Wallet Withdrawal");
        debitEntry.setTransactionType("DEBIT");
        debitEntry.setDepartment(department);
        debitEntry.setBusinessUnit(businessUnit);
        debitEntry.setTransactionDate(LocalDateTime.now());
        accountingClient.postJournalEntry(debitEntry);

        JournalEntryRequest creditEntry = new JournalEntryRequest();
        creditEntry.setAccountCode(creditAccount);
        creditEntry.setAmount(withdrawalAmount);
        creditEntry.setDescription("Transfer to Bank");
        creditEntry.setTransactionType("CREDIT");
        creditEntry.setDepartment(department);
        creditEntry.setBusinessUnit(businessUnit);
        creditEntry.setTransactionDate(LocalDateTime.now());
        accountingClient.postJournalEntry(creditEntry);

        // Recipient Creation
        String recipientCode;
        try {
            recipientCode = paystackTransferService.createRecipient(payload.getAccountName(), payload.getAccountNumber(), payload.getBankCode());
        } catch (Exception e) {
            UpdateWalletBalancePayload updatePayload = new UpdateWalletBalancePayload();
            updatePayload.setWalletId(wallet.getWalletId());
            updatePayload.setAmount(withdrawalAmount.doubleValue());
            walletService.updateWalletBalance(updatePayload, idToken, appUser);

            return aesService.encrypt(gson.toJson(new ErrorResponse("Recipient creation failed",
                    ResponseCode.FAILED_TRANSACTION.getResponseCode())), appUser);
        }

        // Fund Transfer
        String transferCode;
        try {
            transferCode = paystackTransferService.initiateTransfer(withdrawalAmount, recipientCode,
                    "Wallet withdrawal for " + user.getUserName());
        } catch (Exception e) {
            UpdateWalletBalancePayload updatePayload = new UpdateWalletBalancePayload();
            updatePayload.setWalletId(wallet.getWalletId());
            updatePayload.setAmount(withdrawalAmount.doubleValue());
            walletService.updateWalletBalance(updatePayload, idToken, appUser);

            return aesService.encrypt(gson.toJson(new ErrorResponse("Transfer failed",
                    ResponseCode.FAILED_TRANSACTION.getResponseCode())), appUser);
        }

        // Ledger Entry
        LedgerEntry ledgerEntry = new LedgerEntry();
        ledgerEntry.setWalletId(wallet.getWalletId());
        ledgerEntry.setTransactionType(TransactionType.DEBIT);
        ledgerEntry.setAmount(withdrawalAmount);
        ledgerEntry.setStatus(TransactionStatus.PENDING);
        ledgerEntry.setDescription("Wallet Withdrawal - Pending Settlement");
        ledgerEntryRepository.save(ledgerEntry);

        // Settlement Account
        SettlementAccount settlementAccount = settlementAccountRepository.findByAccountNumber(settlementAccountNo);
        if (settlementAccount == null) {
            settlementAccount = new SettlementAccount();
            settlementAccount.setAccountNumber(settlementAccountNo);
            settlementAccount.setBalance(BigDecimal.ZERO);
        }
        settlementAccount.setBalance(settlementAccount.getBalance().add(withdrawalAmount));
        settlementAccountRepository.save(settlementAccount);

        // Final Bank Transfer
        boolean fcmbSuccess = fcmbIntegrationService.transferFunds(settlementAccountNo, withdrawalAmount);
        ledgerEntry.setStatus(fcmbSuccess ? TransactionStatus.COMPLETED : TransactionStatus.FAILED);
        ledgerEntryRepository.save(ledgerEntry);

        if (!fcmbSuccess) {
            UpdateWalletBalancePayload updatePayload = new UpdateWalletBalancePayload();
            updatePayload.setWalletId(wallet.getWalletId());
            updatePayload.setAmount(withdrawalAmount.doubleValue());
            walletService.updateWalletBalance(updatePayload, idToken, appUser);
        }

        // Notification
        notificationService.sendEmail(email, "🔻 Debit Alert - Wallet Withdrawal",
                "Your wallet was debited with ₦" + withdrawalAmount + "\nRef: " + transferCode);

        SuccessResponse response = new SuccessResponse();
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage(fcmbSuccess ? "Withdrawal successful" : "Transfer failed, funds refunded");
        response.setNewBalance(wallet.getBalance());

        return aesService.encrypt(gson.toJson(response), appUser);
    }

    @Override
    @Transactional
    public String freezeWallet(String requestPayload, String idToken, AppUser appUser) {
        FreezeWalletRequestPayload payload = gson.fromJson(requestPayload, FreezeWalletRequestPayload.class);

        // 🔍 Fetch wallet
        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
                .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getWalletId()));

        // 🔐 Ownership check
        if (!wallet.getUserId().equals(appUser.getId())) {
            return gson.toJson(new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

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
        String message = "Your wallet with ID " + payload.getWalletId() + " has been successfully frozen.";
        notificationService.sendEmail(appUser.getEmail(), subject, message);

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
        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
                .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getWalletId()));

        if (!wallet.getUserId().equals(appUser.getId())) {
            return gson.toJson(new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        if (wallet.getStatus() == WalletStatus.ACTIVE) {
            return gson.toJson(new ErrorResponse("Wallet is already active", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        wallet.setStatus(WalletStatus.ACTIVE);
        wallet.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        walletRepository.updateUser(wallet);

        notificationService.sendEmail(appUser.getEmail(), "✅ Wallet Unfrozen",
                "Your wallet with ID " + payload.getWalletId() + " has been reactivated.");

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
        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
                .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getWalletId()));

        if (!wallet.getUserId().equals(appUser.getId())) {
            return gson.toJson(new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        if (wallet.getStatus() == WalletStatus.LOCKED) {
            return gson.toJson(new ErrorResponse("Wallet is already locked", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        wallet.setStatus(WalletStatus.LOCKED);
        wallet.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        walletRepository.updateUser(wallet);

        notificationService.sendEmail(
                appUser.getEmail(),
                "🔒 Wallet Locked",
                "Your wallet with ID " + payload.getWalletId() + " has been locked. Contact support if this was not initiated by you."
        );

        SuccessResponse response = new SuccessResponse();
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("Funds locked successfully");
        response.setNewBalance(wallet.getBalance());

        return gson.toJson(response);
    }

    @Override
    @Transactional
    public String unlockFunds(UnlockFundsRequestPayload payload, String idToken, AppUser appUser) {
        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
                .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getWalletId()));

        if (!wallet.getUserId().equals(appUser.getId())) {
            return gson.toJson(new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        if (wallet.getStatus() != WalletStatus.LOCKED) {
            return gson.toJson(new ErrorResponse("Wallet is not locked", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        wallet.setStatus(WalletStatus.ACTIVE);
        wallet.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        walletRepository.updateUser(wallet);

        notificationService.sendEmail(
                appUser.getEmail(),
                "🔓 Wallet Unlocked",
                "Your wallet with ID " + payload.getWalletId() + " has been unlocked and is now active."
        );

        SuccessResponse response = new SuccessResponse();
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("Funds unlocked successfully");
        response.setNewBalance(wallet.getBalance());

        return gson.toJson(response);
    }

    @Override
    @Transactional
    public String updateWalletBalance(UpdateWalletBalancePayload payload, String idToken, AppUser appUser) throws WalletException {
        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
                .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getWalletId()));

        if (!wallet.getUserId().equals(appUser.getId())) {
            return gson.toJson(new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

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

        LedgerEntry entry = new LedgerEntry();
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

        LedgerEntry entry = new LedgerEntry();
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
        LedgerEntry entry = new LedgerEntry();
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
