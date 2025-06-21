package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.Responses.*;
import com.chh.trustfort.payment.component.AccountingClient;
import com.chh.trustfort.payment.component.ResponseCode;
import com.chh.trustfort.payment.component.WalletUtil;
import com.chh.trustfort.payment.dto.ConfirmBankTransferRequest;
import com.chh.trustfort.payment.dto.JournalEntryRequest;
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
import java.util.List;
import java.util.Locale;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 * @author DOfoleta
 */
@Service
public class WalletServiceImpl implements WalletService {
    private static final Logger log = LoggerFactory.getLogger(WalletServiceImpl.class);

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UsersService usersService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private WalletUtil walletUtil;

    @Autowired
    private  AuditLogService auditLogService;

    @Autowired
    private AesService aesService;

    @Autowired
    private PinService pinService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AccountingClient accountingClient;

    @Autowired
    private NotificationService notificationService;


//    @Autowired
//    private FCMBIntegrationService fcmbIntegrationService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LedgerEntryRepository ledgerEntryRepository;

    @Autowired
    private SettlementAccountRepository settlementAccountRepository;

    @Autowired
    private FraudDetectionService fraudDetectionService;

    @Autowired
    private MockFCMBIntegrationService fcmbIntegrationService;

    @Autowired
    private PaymentReferenceRepository paymentReferenceRepository;

    @Autowired
    private PaystackTransferService paystackTransferService;



    @Autowired
    private Gson gson;

    private Wallet getWalletOrThrow(String walletId) {
        return walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new WalletException("Wallet not found for ID: " + walletId));
    }



//    @Override
//    public String createWallet(@Valid CreateWalletRequestPayload requestPayload, Users user) {
//        log.info("Creating wallet for user ID: {}", user.getId());
//
//        CreateWalletResponsePayload oResponse = new CreateWalletResponsePayload();
//        oResponse.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());
//        oResponse.setResponseMessage(messageSource.getMessage("failed", null, Locale.ENGLISH));
//
//        // ✅ Validate user
//        Users users = usersService.getUserById(user.getId());
//        if (users == null) {
//            log.warn("User not found: {}", user.getId());
//            oResponse.setResponseMessage(messageSource.getMessage("user.not.found", null, Locale.ENGLISH));
//            return aesService.encrypt(gson.toJson(oResponse), String.valueOf(user));
//        }
//
//        // ✅ Check if the user already has a wallet
//        if (walletRepository.existsByOwner(users)) {
//            log.warn("Wallet already exists for user ID: {}", user.getId());
//            oResponse.setResponseMessage(messageSource.getMessage("wallet.already.exists", null, Locale.ENGLISH));
//            return aesService.encrypt(gson.toJson(oResponse), user.getEcred());
//        }
//
//        // ✅ Generate Wallet ID and serial number
//        String generatedId = walletRepository.generateWalletId();
//        String serialNumber = generatedId.replace("WAL-", "");
//
//        // ✅ Create and save wallet
//        Wallet wallet = new Wallet();
//        wallet.setWalletId(generatedId);
//        wallet.setSerialNumber(Long.parseLong(serialNumber));
//        wallet.setUsers(users);
//        wallet.setCurrency(requestPayload.getCurrency());
//        wallet.setBalance(BigDecimal.ZERO);
//        wallet.setStatus(WalletStatus.ACTIVE);
//        wallet = walletRepository.createWallet(wallet);
//
//        log.info("Wallet created successfully with ID: {}", wallet.getWalletId());
//
//        if (walletUtil.validateWalletId(wallet.getWalletId())) {
//            oResponse.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//            oResponse.setResponseMessage(messageSource.getMessage("wallet.created.success", null, Locale.ENGLISH));
//        }
//
//        return aesService.encrypt(gson.toJson(oResponse), users.getEcred());
//    }

    @Override
    public Wallet createWallet(String userId, String emailAddress) {
        log.info("Creating wallet for external user ID: {}", userId);

        if (walletRepository.existsByUserId(userId)) {
            throw new WalletException("Wallet already exists for user ID: " + userId);
        }

        String walletId = walletRepository.generateWalletId();

        Wallet wallet = new Wallet();
        wallet.setWalletId(walletId);
        wallet.setUserId(userId); // This must exist in the Wallet entity
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatus(WalletStatus.ACTIVE);
        wallet.setCurrency("NGN"); // Or make this dynamic if needed

        return walletRepository.createWallet(wallet);

    }

//    @Override
//    @Transactional
//    public String fundWallet(FundWalletRequestPayload payload, Users users) {
//        log.info("Funding wallet for user ID: {}", users.getId());
//
//        // Retrieve wallet from repository using the wallet ID from the payload
//        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
//                .orElseThrow(() -> new WalletException("Wallet not found for ID: " + payload.getWalletId()));
//
//        if (wallet.getStatus() == WalletStatus.SUSPENDED || wallet.getStatus() == WalletStatus.CLOSED) {
//            log.warn("Cannot fund wallet: Wallet status is {}", wallet.getStatus());
//            ErrorResponse errorResponse = new ErrorResponse("Wallet is not active", ResponseCode.FAILED_TRANSACTION.getResponseCode());
//            return gson.toJson(errorResponse);
//        }
//
//        // Ensure the wallet belongs to the requesting user
//        if (!wallet.getUsers().getId().equals(users.getId())) {
//            log.warn("Unauthorized access to wallet ID: {}", payload.getWalletId());
//            ErrorResponse errorResponse = new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode());
////            return aesService.encrypt(gson.toJson(errorResponse), users.getEcred());
//
//            return gson.toJson(errorResponse);
//        }
//
//        // Credit the wallet: add the provided amount to the existing balance
//        BigDecimal creditAmount = payload.getAmount();
//        wallet.setBalance(wallet.getBalance().add(creditAmount));
//
//        // Persist the updated wallet balance
//        walletRepository.updateUser(wallet);
//
//        // Create a ledger entry for this funding transaction
//        LedgerEntry ledgerEntry = new LedgerEntry();
//        ledgerEntry.setWalletId(wallet.getWalletId());
//        ledgerEntry.setTransactionType(TransactionType.CREDIT);
//        ledgerEntry.setAmount(creditAmount);
//        ledgerEntry.setStatus(TransactionStatus.COMPLETED);
//        ledgerEntry.setDescription("Wallet Funding");
//        ledgerEntryRepository.save(ledgerEntry);
//
//        // Create and send journal entry
//        JournalEntryRequest journal = new JournalEntryRequest();
//        journal.setAccountCode("REV001"); // Replace with actual revenue account code
//        journal.setAmount(creditAmount);
//        journal.setDescription("Wallet Funding");
//        journal.setTransactionType("CREDIT");
//        journal.setDepartment("Wallet");
//        journal.setBusinessUnit("Retail");
//        journal.setTransactionDate(LocalDateTime.now());
//
//        accountingClient.postJournalEntry(journal);
//
//
//        // Prepare success response
//        SuccessResponse response = new SuccessResponse();
//        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//        response.setResponseMessage("Wallet funded successfully");
//        response.setNewBalance(wallet.getBalance());
//
//        notificationService.sendEmail(users.getEmailAddress(),
//                "🔺 Wallet Funded",
//                "Your wallet was credited with ₦" + creditAmount + " via manual funding.");
//
//        log.info("Wallet ID: {} funded successfully. New balance: {}", wallet.getWalletId(), wallet.getBalance());
////        return aesService.encrypt(gson.toJson(response), users.getEcred());
//        return gson.toJson(response);
//    }
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

//    @Override
//    public String fetchWallet(String walletId, Users users) {
//        Wallet wallet = walletRepository.findByWalletId(walletId)
//                .orElseThrow(() -> new WalletException("Wallet not found for ID: " + walletId));
//        if (wallet == null) {
//            ErrorResponse errorResponse = new ErrorResponse("Wallet not found", ResponseCode.FAILED_TRANSACTION.getResponseCode());
//            return aesService.encrypt(gson.toJson(errorResponse), users.getEcred());
//        }
//        if (!wallet.getUsers().getId().equals(users.getId())) {
//            ErrorResponse errorResponse = new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode());
//            return aesService.encrypt(gson.toJson(errorResponse), users.getEcred());
//        }
////        return aesService.encrypt(gson.toJson(wallet), users.getEcred());
//        return new WalletResponse(ResponseCode.SUCCESS.getResponseCode(), "Wallet retrieved successfully", wallet).toString();
//    }
    @Override
    public String fetchWallet(String walletId, String userId) {
        Wallet wallet = walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new WalletException("Wallet not found for ID: " + walletId));

        if (!wallet.getUserId().equals(userId)) {
            ErrorResponse errorResponse = new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode());
            return gson.toJson(errorResponse);
        }

        return gson.toJson(new WalletResponse(ResponseCode.SUCCESS.getResponseCode(), "Wallet retrieved successfully", wallet));
    }


//    @Override
//    public String transferFunds(FundsTransferRequestPayload payload, Users users) {
//        // Simplistic implementation for demo:
//        // 1. Retrieve sender and receiver wallets.
//        // 2. Validate ownership and sufficient funds.
//        // 3. Debit sender and credit receiver using updateWalletBalance.
//        // 4. Record ledger entries for both debit and credit.
//        // 5. Return success response.
//
//        // Retrieve sender wallet
//        Wallet senderWallet = walletRepository.findByWalletId(payload.getSenderWalletId())
//                .orElseThrow(() -> new WalletException("Sender wallet not found: " + payload.getSenderWalletId()));
//
//        if (!senderWallet.getUsers().getId().equals(users.getId())) {
//            ErrorResponse errorResponse = new ErrorResponse("Unauthorized access to sender wallet", ResponseCode.FAILED_TRANSACTION.getResponseCode());
//            return aesService.encrypt(gson.toJson(errorResponse), users.getEcred());
//        }
//
//        // Validate Transaction PIN
//        if (!pinService.matches(payload.getTransactionPin(), users.getTransactionPin())) {
//            log.warn("Invalid PIN for user {}", users.getUserName());
//            ErrorResponse errorResponse = new ErrorResponse("Invalid transaction PIN", ResponseCode.FAILED_TRANSACTION.getResponseCode());
//            return aesService.encrypt(gson.toJson(errorResponse), users.getEcred());
//        }
//
//        // Retrieve receiver wallet
//        Wallet receiverWallet = walletRepository.findByWalletId(payload.getReceiverWalletId())
//                .orElseThrow(() -> new WalletException("Receiver wallet not found: " + payload.getReceiverWalletId()));
//
//        BigDecimal transferAmount = payload.getAmount();
//        if (senderWallet.getBalance().compareTo(transferAmount) < 0) {
//            ErrorResponse errorResponse = new ErrorResponse("Insufficient funds in sender wallet", ResponseCode.FAILED_TRANSACTION.getResponseCode());
//            return aesService.encrypt(gson.toJson(errorResponse), users.getEcred());
//        }
//
//        // Debit sender
//        try {
//            updateWalletBalance(senderWallet.getWalletId(), transferAmount.negate().doubleValue());
//        } catch (WalletException e) {
//            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), ResponseCode.FAILED_TRANSACTION.getResponseCode());
//            return aesService.encrypt(gson.toJson(errorResponse), users.getEcred());
//        }
//        // Credit receiver
//        try {
//            updateWalletBalance(receiverWallet.getWalletId(), transferAmount.doubleValue());
//        } catch (WalletException e) {
//            // In case of failure, rollback sender debit (simplistic rollback)
//            try {
//                updateWalletBalance(senderWallet.getWalletId(), transferAmount.doubleValue());
//            } catch (WalletException ex) {
//                log.error("Rollback failed: {}", ex.getMessage());
//            }
//            ErrorResponse errorResponse = new ErrorResponse("Failed to credit receiver wallet", ResponseCode.FAILED_TRANSACTION.getResponseCode());
//            return aesService.encrypt(gson.toJson(errorResponse), users.getEcred());
//        }
//
//        LedgerEntry senderEntry = new LedgerEntry();
//        senderEntry.setWalletId(senderWallet.getWalletId());
//        senderEntry.setTransactionType(TransactionType.DEBIT);
//        senderEntry.setAmount(transferAmount);
//        senderEntry.setStatus(TransactionStatus.COMPLETED);
//        senderEntry.setDescription("Transfer to " + receiverWallet.getWalletId());
//
//        LedgerEntry receiverEntry = new LedgerEntry();
//        receiverEntry.setWalletId(receiverWallet.getWalletId());
//        receiverEntry.setTransactionType(TransactionType.CREDIT);
//        receiverEntry.setAmount(transferAmount);
//        receiverEntry.setStatus(TransactionStatus.COMPLETED);
//        receiverEntry.setDescription("Transfer from " + senderWallet.getWalletId());
//
//        ledgerEntryRepository.save(senderEntry);
//        ledgerEntryRepository.save(receiverEntry);
//
//        SuccessResponse response = new SuccessResponse();
//        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//        response.setResponseMessage("Transfer processed successfully");
//        response.setNewBalance(senderWallet.getBalance()); // Optionally, return updated sender balance
//        // DEBIT sender
//        JournalEntryRequest senderDebit = new JournalEntryRequest();
//        senderDebit.setAccountCode("WALLET001"); // e.g., Sender Wallet Account
//        senderDebit.setAmount(payload.getAmount());
//        senderDebit.setDescription("Transfer to " + payload.getReceiverWalletId());
//        senderDebit.setTransactionType("DEBIT");
//        senderDebit.setDepartment("Wallet");
//        senderDebit.setBusinessUnit("Retail");
//        senderDebit.setTransactionDate(LocalDateTime.now());
//
//        accountingClient.postJournalEntry(senderDebit);
//
//// CREDIT receiver
//        JournalEntryRequest receiverCredit = new JournalEntryRequest();
//        receiverCredit.setAccountCode("WALLET001"); // e.g., Receiver Wallet Account
//        receiverCredit.setAmount(payload.getAmount());
//        receiverCredit.setDescription("Transfer from " + payload.getSenderWalletId());
//        receiverCredit.setTransactionType("CREDIT");
//        receiverCredit.setDepartment("Wallet");
//        receiverCredit.setBusinessUnit("Retail");
//        receiverCredit.setTransactionDate(LocalDateTime.now());
//
//        accountingClient.postJournalEntry(receiverCredit);
//
//        // Sender
//        notificationService.sendEmail(users.getEmailAddress(),
//                "🔻 Wallet Debit - Transfer",
//                "You transferred ₦" + transferAmount + " to " + receiverWallet.getWalletId());
//
//// Receiver
//        notificationService.sendEmail(receiverWallet.getUsers().getEmailAddress(),
//                "🔺 Wallet Credit - Incoming Transfer",
//                "You received ₦" + transferAmount + " from " + senderWallet.getWalletId());
//
////        return aesService.encrypt(gson.toJson(response), users.getEcred());
//        return gson.toJson(response);
//
//
//    }

    @Override
    public String transferFunds(FundsTransferRequestPayload payload, String userId, String email) {
        // ✅ Fetch sender wallet
        Wallet senderWallet = walletRepository.findByWalletId(payload.getSenderWalletId())
                .orElseThrow(() -> new WalletException("Sender wallet not found: " + payload.getSenderWalletId()));

        if (!senderWallet.getUserId().equals(userId)) {
            return gson.toJson(new ErrorResponse("Unauthorized access to sender wallet", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        // ✅ Validate Transaction PIN
        Users user = usersRepository.findByEmailAddress(email)
                .orElseThrow(() -> new WalletException("User not found: " + email));
        if (!pinService.matches(payload.getTransactionPin(), user.getTransactionPin())) {
            log.warn("Invalid PIN for user {}", user.getUserName());
            return gson.toJson(new ErrorResponse("Invalid transaction PIN", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        // ✅ Fetch receiver wallet
        Wallet receiverWallet = walletRepository.findByWalletId(payload.getReceiverWalletId())
                .orElseThrow(() -> new WalletException("Receiver wallet not found: " + payload.getReceiverWalletId()));

        BigDecimal transferAmount = payload.getAmount();
        if (senderWallet.getBalance().compareTo(transferAmount) < 0) {
            return gson.toJson(new ErrorResponse("Insufficient funds in sender wallet", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        // ✅ Perform transfer
        updateWalletBalance(senderWallet.getWalletId(), transferAmount.negate().doubleValue());
        updateWalletBalance(receiverWallet.getWalletId(), transferAmount.doubleValue());

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
        JournalEntryRequest debitEntry = new JournalEntryRequest();
        debitEntry.setAccountCode("WALLET001");
        debitEntry.setAmount(transferAmount);
        debitEntry.setDescription("Transfer to " + receiverWallet.getWalletId());
        debitEntry.setTransactionType("DEBIT");
        debitEntry.setDepartment("Wallet");
        debitEntry.setBusinessUnit("Retail");
        debitEntry.setTransactionDate(LocalDateTime.now());
        accountingClient.postJournalEntry(debitEntry);

        JournalEntryRequest creditEntry = new JournalEntryRequest();
        creditEntry.setAccountCode("WALLET001");
        creditEntry.setAmount(transferAmount);
        creditEntry.setDescription("Transfer from " + senderWallet.getWalletId());
        creditEntry.setTransactionType("CREDIT");
        creditEntry.setDepartment("Wallet");
        creditEntry.setBusinessUnit("Retail");
        creditEntry.setTransactionDate(LocalDateTime.now());
        accountingClient.postJournalEntry(creditEntry);

        // ✅ Notifications
        notificationService.sendEmail(
                email,
                "🔻 Wallet Debit - Transfer",
                "You transferred ₦" + transferAmount + " to " + receiverWallet.getWalletId()
        );
        notificationService.sendEmail(
                receiverWallet.getUsers().getEmailAddress(),
                "🔺 Wallet Credit - Incoming Transfer",
                "You received ₦" + transferAmount + " from " + senderWallet.getWalletId()
        );

        // ✅ Build success response
        SuccessResponse response = new SuccessResponse();
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("Transfer processed successfully");
        response.setNewBalance(senderWallet.getBalance());

        return gson.toJson(response);
    }


//    @Override
//    public WalletBalanceResponse getWalletBalance(String walletId, Users users) {
//        log.info("Fetching balance for Wallet ID: {}", walletId);
//
//        WalletBalanceResponse response = new WalletBalanceResponse();
//        response.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());
//
//        // ✅ Retrieve wallet from repository
//        Wallet wallet = getWalletOrThrow(walletId);
//
//        if (wallet.getStatus() == WalletStatus.SUSPENDED) {
//            log.warn("Balance check blocked: Wallet is frozen (SUSPENDED): {}", walletId);
//            response.setMessage("Wallet is frozen and cannot be accessed");
//            return response;
//        }
//
//
//        // ✅ Ensure wallet belongs to user
//        if (!wallet.getUsers().getId().equals(users.getId())) {
//            log.warn("Unauthorized access to wallet ID: {}", walletId);
//            response.setMessage("Unauthorized access to this wallet");
////            return aesService.encrypt(gson.toJson(response), users.getEcred());
//            return response;
//        }
//
//        // ✅ Validate wallet status before returning balance
//        if (wallet.getStatus() == WalletStatus.CLOSED) {
//            response.setMessage("Wallet is closed and cannot be accessed");
////            return aesService.encrypt(gson.toJson(response), users.getEcred());
//            return response;
//        }
//
//        // ✅ Construct response
//        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//        response.setMessage("Balance retrieved successfully");
//        response.setWalletId(walletId);
//        response.setBalance(wallet.getBalance());
//
//        // ✅ Debugging log for encryption key
//        log.info("User encryption key (ecred): {}", users.getEcred());
//
//        // ✅ Ensure encryption key format is valid
//        if (users.getEcred() == null || !users.getEcred().contains("/")) {
//            log.error("Invalid encryption key format: {}", users.getEcred());
//            response.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());
//            response.setMessage("Encryption key format is invalid");
////            return gson.toJson(response);  // Send response without encryption if key is invalid
//            return response;
//        }
//
////        return aesService.encrypt(gson.toJson(response), users.getEcred());
//        return response;
//    }

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

//    @Override
//    public ResponseEntity<List<LedgerEntry>> getTransactionHistory(String walletId, LocalDate startDate, LocalDate endDate, Users users) {
//        // Simplistic implementation: retrieve ledger entries by walletId (ignoring dates for now)
//        java.util.List<LedgerEntry> transactions = ledgerEntryRepository.findByWalletId(walletId);
////        return aesService.encrypt(gson.toJson(transactions), users.getEcred());
//        return ResponseEntity.ok(transactions);
//    }

    @Override
    public ResponseEntity<List<LedgerEntry>> getTransactionHistory(String walletId, LocalDate startDate, LocalDate endDate, String userId) {
        Wallet wallet = walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new WalletException("Wallet not found for ID: " + walletId));

        if (!wallet.getUserId().equals(userId)) {
            log.warn("Unauthorized access to transaction history for wallet ID: {}", walletId);
            throw new WalletException("Unauthorized access to this wallet");
        }

        List<LedgerEntry> transactions = ledgerEntryRepository.findByWalletId(walletId);
        return ResponseEntity.ok(transactions);
    }

//    @Override
//    @Transactional
//    public String withdrawFunds(WithdrawFundsRequestPayload payload, Users users) {
//        log.info("Processing withdrawal for wallet ID: {}", payload.getWalletId());
//
//        // 🔐 OTP Validation
//        if (!otpService.validateOtp(users.getId(), String.valueOf(payload.getOtpCode()), "WITHDRAW_FUNDS")) {
//            log.warn("Invalid or expired OTP for user: {}", users.getUserName());
//            ErrorResponse errorResponse = new ErrorResponse("Invalid or expired OTP", ResponseCode.FAILED_TRANSACTION.getResponseCode());
////            return aesService.encrypt(gson.toJson(errorResponse), users.getEcred());
//            return gson.toJson(errorResponse);
//        }
//
//        // Validate Transaction PIN
//        if (!pinService.matches(payload.getTransactionPin(), users.getTransactionPin())) {
//            log.warn("Invalid PIN attempt for user {}", users.getUserName());
//            ErrorResponse errorResponse = new ErrorResponse("Invalid transaction PIN", ResponseCode.FAILED_TRANSACTION.getResponseCode());
//            return gson.toJson(errorResponse);
//        }
//
//
//        // Retrieve the wallet using the wallet ID
//        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
//                .orElseThrow(() -> new WalletException("Wallet not found for ID: " + payload.getWalletId()));
//
//
//        // Ensure the wallet belongs to the authenticated user
//        if (!wallet.getUsers().getId().equals(users.getId())) {
//            log.warn("Unauthorized access to wallet ID: {}", payload.getWalletId());
//            ErrorResponse errorResponse = new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode());
////            return aesService.encrypt(gson.toJson(errorResponse), users.getEcred());
//            return gson.toJson(errorResponse);
//        }
//
//        // ❗️Add wallet status check here
//        if (wallet.getStatus() == WalletStatus.SUSPENDED || wallet.getStatus() == WalletStatus.LOCKED || wallet.getStatus() == WalletStatus.CLOSED) {
//            log.warn("Attempt to withdraw from wallet in invalid state: {}", wallet.getStatus());
//            ErrorResponse errorResponse = new ErrorResponse("Withdrawal not allowed. Wallet is currently " + wallet.getStatus().name(), ResponseCode.FAILED_TRANSACTION.getResponseCode());
////            return aesService.encrypt(gson.toJson(errorResponse), users.getEcred());
//            return gson.toJson(errorResponse);
//        }
//
//        // Fraud Detection Check
//        if (fraudDetectionService.isFraudulentWithdrawal(users, payload.getAmount())) {
//            log.warn("Fraud detected for user {}: blocked withdrawal", users.getUserName());
//            ErrorResponse errorResponse = new ErrorResponse("Suspicious transaction: Limit exceeded", ResponseCode.FAILED_TRANSACTION.getResponseCode());
//            return aesService.encrypt(gson.toJson(errorResponse), users.getEcred());
//        }
//
//
//        // Debit the wallet: check sufficient funds before withdrawal
//        BigDecimal withdrawalAmount = payload.getAmount();
//        if (wallet.getBalance().compareTo(withdrawalAmount) < 0) {
//            log.warn("Insufficient funds in wallet ID: {}", payload.getWalletId());
//            ErrorResponse errorResponse = new ErrorResponse("Insufficient funds", ResponseCode.FAILED_TRANSACTION.getResponseCode());
////            return aesService.encrypt(gson.toJson(errorResponse), users.getEcred());
//            return gson.toJson(errorResponse);
//        }
//
//        // Subtract the withdrawal amount from the wallet's balance
//        wallet.setBalance(wallet.getBalance().subtract(withdrawalAmount));
//
//        JournalEntryRequest debitEntry = new JournalEntryRequest();
//        debitEntry.setAccountCode("CASH001"); // E.g., Cash Account
//        debitEntry.setAmount(payload.getAmount());
//        debitEntry.setDescription("Wallet Withdrawal");
//        debitEntry.setTransactionType("DEBIT");
//        debitEntry.setDepartment("Wallet");
//        debitEntry.setBusinessUnit("Retail");
//        debitEntry.setTransactionDate(LocalDateTime.now());
//
//        accountingClient.postJournalEntry(debitEntry);
//
//
//        JournalEntryRequest creditEntry = new JournalEntryRequest();
//        creditEntry.setAccountCode("BANK001"); // Settlement account code
//        creditEntry.setAmount(payload.getAmount());
//        creditEntry.setDescription("Transfer to FCMB");
//        creditEntry.setTransactionType("CREDIT");
//        creditEntry.setDepartment("Wallet");
//        creditEntry.setBusinessUnit("Retail");
//        creditEntry.setTransactionDate(LocalDateTime.now());
//
//        accountingClient.postJournalEntry(creditEntry);
//
//
//        // Persist the updated wallet balance
//        walletRepository.updateUser(wallet);
//
//        String recipientCode;
//        try {
//            recipientCode = paystackTransferService.createRecipient(
//                    payload.getAccountName(),         // ✔️ name
//                    payload.getAccountNumber(),       // ✔️ account number
//                    payload.getBankCode()             // ✔️ bank code (e.g., "044" for Access Bank)
//            );
//            log.info("Recipient created: {}", recipientCode);
//        } catch (Exception e) {
//            log.error("Failed to create Paystack recipient", e);
//            return gson.toJson(new ErrorResponse("Failed to create recipient", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
//        }
//
//// ✅ Call Paystack to initiate transfer
//        String transferCode;
//        try {
//            transferCode = paystackTransferService.initiateTransfer(
//                    withdrawalAmount,
//                    recipientCode,
//                    "Wallet withdrawal for " + users.getUserName()
//            );
//            log.info("Transfer initiated. Code: {}", transferCode);
//        } catch (Exception e) {
//            log.error("Paystack transfer failed", e);
//            // Rollback wallet balance
//            updateWalletBalance(wallet.getWalletId(), withdrawalAmount.doubleValue());
//            return gson.toJson(new ErrorResponse("Withdrawal failed at transfer stage", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
//        }
//
//        // Create a ledger entry for this withdrawal (pending status)
//        LedgerEntry ledgerEntry = new LedgerEntry();
//        ledgerEntry.setWalletId(wallet.getWalletId());
//        ledgerEntry.setTransactionType(com.chh.trustfort.payment.enums.TransactionType.DEBIT);
//        ledgerEntry.setAmount(withdrawalAmount);
//        ledgerEntry.setStatus(com.chh.trustfort.payment.enums.TransactionStatus.PENDING);
//        ledgerEntry.setDescription("Wallet Withdrawal - Pending Settlement");
//        ledgerEntryRepository.save(ledgerEntry);
//
//        // Settlement Process: Move funds to the settlement account
//        String settlementAccountNumber = "MOCK-FCMB-001"; // Pre-configured settlement account number
//        SettlementAccount settlementAccount = settlementAccountRepository.findByAccountNumber(settlementAccountNumber);
//        if (settlementAccount == null) {
//            settlementAccount = new SettlementAccount();
//            settlementAccount.setAccountNumber(settlementAccountNumber);
//            settlementAccount.setBalance(BigDecimal.ZERO);
//        }
//        settlementAccount.setBalance(settlementAccount.getBalance().add(withdrawalAmount));
//        settlementAccountRepository.save(settlementAccount);
//
//        // Call FCMB API to process the external transfer
//        boolean fcmbSuccess = fcmbIntegrationService.transferFunds(settlementAccountNumber, withdrawalAmount);
//        if (fcmbSuccess) {
//            ledgerEntry.setStatus(TransactionStatus.COMPLETED);
//        } else {
//            updateWalletBalance(wallet.getWalletId(), withdrawalAmount.doubleValue()); // Rollback
//            ledgerEntry.setStatus(TransactionStatus.FAILED);
//        }
//        ledgerEntryRepository.save(ledgerEntry);
//
//        String subject = "🔻 Debit Alert - Wallet Withdrawal";
//        String body = "Dear " + users.getWalletId() + ",\n\n" +
//                "Your wallet has been debited with ₦" + withdrawalAmount + " for withdrawal request.\n" +
//                "New Balance: ₦" + wallet.getBalance() + "\n\n" +
//                "Reference: " + transferCode + "\nThank you.";
//
//        notificationService.sendEmail(users.getEmailAddress(), subject, body);
//
//
//        Wallet updatedWallet = walletRepository.findByWalletId(payload.getWalletId())
//                .orElseThrow(() -> new WalletException("Wallet not found after processing"));
//
//        // Prepare success response
//        SuccessResponse response = new SuccessResponse();
//        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//        response.setResponseMessage(fcmbSuccess ? "Withdrawal processed successfully" : "Withdrawal failed, funds refunded");
//        response.setNewBalance(updatedWallet.getBalance());
//        log.info("Withdrawal processed for wallet ID: {}. New balance: {}", wallet.getWalletId(), wallet.getBalance());
////        return aesService.encrypt(gson.toJson(response), users.getEcred());
//        return gson.toJson(response);
//
//
//    }

    @Override
    @Transactional
    public String withdrawFunds(WithdrawFundsRequestPayload payload, String userId, String email) {
        log.info("Processing withdrawal for wallet ID: {}", payload.getWalletId());

        // 🔍 Retrieve user
        Users user = usersRepository.findByEmailAddress(email)
                .orElseThrow(() -> new WalletException("User not found: " + email));

        // 🔐 OTP Validation
        if (!otpService.validateOtp(user.getId(), String.valueOf(payload.getOtpCode()), "WITHDRAW_FUNDS")) {
            log.warn("Invalid or expired OTP for user: {}", user.getUserName());
            return gson.toJson(new ErrorResponse("Invalid or expired OTP", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        // 🔐 PIN Validation
        if (!pinService.matches(payload.getTransactionPin(), user.getTransactionPin())) {
            log.warn("Invalid PIN for user {}", user.getUserName());
            return gson.toJson(new ErrorResponse("Invalid transaction PIN", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        // 🧾 Retrieve Wallet
        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
                .orElseThrow(() -> new WalletException("Wallet not found for ID: " + payload.getWalletId()));

        if (!wallet.getUserId().equals(userId)) {
            return gson.toJson(new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        if (wallet.getStatus() == WalletStatus.SUSPENDED || wallet.getStatus() == WalletStatus.LOCKED || wallet.getStatus() == WalletStatus.CLOSED) {
            return gson.toJson(new ErrorResponse("Withdrawal not allowed. Wallet is " + wallet.getStatus(), ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        // 🚨 Fraud Detection
        if (fraudDetectionService.isFraudulentWithdrawal(user, payload.getAmount())) {
            return gson.toJson(new ErrorResponse("Suspicious transaction: Limit exceeded", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        // 💰 Balance Check
        BigDecimal withdrawalAmount = payload.getAmount();
        if (wallet.getBalance().compareTo(withdrawalAmount) < 0) {
            return gson.toJson(new ErrorResponse("Insufficient funds", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        // 💸 Debit Wallet
        wallet.setBalance(wallet.getBalance().subtract(withdrawalAmount));
        walletRepository.updateUser(wallet);

        // 📘 Journal Entries
        JournalEntryRequest debitEntry = new JournalEntryRequest();
        debitEntry.setAccountCode("CASH001");
        debitEntry.setAmount(withdrawalAmount);
        debitEntry.setDescription("Wallet Withdrawal");
        debitEntry.setTransactionType("DEBIT");
        debitEntry.setDepartment("Wallet");
        debitEntry.setBusinessUnit("Retail");
        debitEntry.setTransactionDate(LocalDateTime.now());
        accountingClient.postJournalEntry(debitEntry);

        JournalEntryRequest creditEntry = new JournalEntryRequest();
        creditEntry.setAccountCode("BANK001");
        creditEntry.setAmount(withdrawalAmount);
        creditEntry.setDescription("Transfer to FCMB");
        creditEntry.setTransactionType("CREDIT");
        creditEntry.setDepartment("Wallet");
        creditEntry.setBusinessUnit("Retail");
        creditEntry.setTransactionDate(LocalDateTime.now());
        accountingClient.postJournalEntry(creditEntry);

        // 🏦 Create Paystack Recipient
        String recipientCode;
        try {
            recipientCode = paystackTransferService.createRecipient(
                    payload.getAccountName(),
                    payload.getAccountNumber(),
                    payload.getBankCode()
            );
        } catch (Exception e) {
            updateWalletBalance(wallet.getWalletId(), withdrawalAmount.doubleValue()); // Rollback
            return gson.toJson(new ErrorResponse("Recipient creation failed", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        // 🔁 Initiate Transfer
        String transferCode;
        try {
            transferCode = paystackTransferService.initiateTransfer(
                    withdrawalAmount,
                    recipientCode,
                    "Wallet withdrawal for " + user.getUserName()
            );
        } catch (Exception e) {
            updateWalletBalance(wallet.getWalletId(), withdrawalAmount.doubleValue()); // Rollback
            return gson.toJson(new ErrorResponse("Transfer failed", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        // 📒 Ledger Entry
        LedgerEntry ledgerEntry = new LedgerEntry();
        ledgerEntry.setWalletId(wallet.getWalletId());
        ledgerEntry.setTransactionType(TransactionType.DEBIT);
        ledgerEntry.setAmount(withdrawalAmount);
        ledgerEntry.setStatus(TransactionStatus.PENDING);
        ledgerEntry.setDescription("Wallet Withdrawal - Pending Settlement");
        ledgerEntryRepository.save(ledgerEntry);

        // 💼 Settlement Process
        SettlementAccount settlementAccount = settlementAccountRepository.findByAccountNumber("MOCK-FCMB-001");
        if (settlementAccount == null) {
            settlementAccount = new SettlementAccount();
            settlementAccount.setAccountNumber("MOCK-FCMB-001");
            settlementAccount.setBalance(BigDecimal.ZERO);
        }
        settlementAccount.setBalance(settlementAccount.getBalance().add(withdrawalAmount));
        settlementAccountRepository.save(settlementAccount);

        // 🏦 FCMB Integration
        boolean fcmbSuccess = fcmbIntegrationService.transferFunds("MOCK-FCMB-001", withdrawalAmount);
        ledgerEntry.setStatus(fcmbSuccess ? TransactionStatus.COMPLETED : TransactionStatus.FAILED);
        ledgerEntryRepository.save(ledgerEntry);

        if (!fcmbSuccess) {
            updateWalletBalance(wallet.getWalletId(), withdrawalAmount.doubleValue()); // Rollback
        }

        // 📧 Notify
        String subject = "🔻 Debit Alert - Wallet Withdrawal";
        String message = "Your wallet was debited with ₦" + withdrawalAmount + "\nRef: " + transferCode;
        notificationService.sendEmail(email, subject, message);

        // ✅ Success Response
        SuccessResponse response = new SuccessResponse();
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage(fcmbSuccess ? "Withdrawal successful" : "Transfer failed, funds refunded");
        response.setNewBalance(wallet.getBalance());

        return gson.toJson(response);
    }

//    @Override
//    public String freezeWallet(FreezeWalletRequestPayload payload, Users users) {
//        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
//                .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getWalletId()));
//
//        if (!wallet.getUsers().getId().equals(users.getId())) {
//            ErrorResponse errorResponse = new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode());
//            return aesService.encrypt(gson.toJson(errorResponse), users.getEcred());
//        }
//        wallet.setStatus(WalletStatus.SUSPENDED);
//        walletRepository.updateUser(wallet);
//        SuccessResponse response = new SuccessResponse();
//        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//        response.setResponseMessage("Wallet frozen successfully");
//        response.setNewBalance(wallet.getBalance());
////        return aesService.encrypt(gson.toJson(response), users.getEcred());
//        return gson.toJson(response);
//    }
@Override
@Transactional
public String freezeWallet(FreezeWalletRequestPayload payload, String userId, String email) {
    Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
            .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getWalletId()));

    if (!wallet.getUserId().equals(userId)) {
        return gson.toJson(new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
    }

    wallet.setStatus(WalletStatus.SUSPENDED);
    walletRepository.updateUser(wallet);

    SuccessResponse response = new SuccessResponse();
    response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
    response.setResponseMessage("Wallet frozen successfully");
    response.setNewBalance(wallet.getBalance());

    return gson.toJson(response);
}

//    @Override
//    public String unfreezeWallet(UnfreezeWalletRequestPayload payload, Users users) {
//        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
//                .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getWalletId()));
//
//        if (!wallet.getUsers().getId().equals(users.getId())) {
//            ErrorResponse errorResponse = new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode());
//            return aesService.encrypt(gson.toJson(errorResponse), users.getEcred());
//        }
//        wallet.setStatus(WalletStatus.ACTIVE);
//        walletRepository.updateUser(wallet);
//        SuccessResponse response = new SuccessResponse();
//        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//        response.setResponseMessage("Wallet unfrozen successfully");
//        response.setNewBalance(wallet.getBalance());
////        return aesService.encrypt(gson.toJson(response), users.getEcred());
//        return gson.toJson(response);
//    }
@Override
@Transactional
public String unfreezeWallet(UnfreezeWalletRequestPayload payload, String userId, String email) {
    Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
            .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getWalletId()));

    if (!wallet.getUserId().equals(userId)) {
        return gson.toJson(new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
    }

    wallet.setStatus(WalletStatus.ACTIVE);
    walletRepository.updateUser(wallet);

    SuccessResponse response = new SuccessResponse();
    response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
    response.setResponseMessage("Wallet unfrozen successfully");
    response.setNewBalance(wallet.getBalance());

    return gson.toJson(response);
}

//    @Override
//    public String closeWallet(CloseWalletRequestPayload payload, Users users) {
//        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
//                .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getWalletId()));
//
//        if (!wallet.getUsers().getId().equals(users.getId())) {
//            ErrorResponse errorResponse = new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode());
//            return aesService.encrypt(gson.toJson(errorResponse), users.getEcred());
//        }
//        wallet.setStatus(WalletStatus.CLOSED);
//        walletRepository.updateUser(wallet);
//        SuccessResponse response = new SuccessResponse();
//        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//        response.setResponseMessage("Wallet closed successfully");
//        response.setNewBalance(wallet.getBalance());
////        return aesService.encrypt(gson.toJson(response), users.getEcred());
//        return gson.toJson(response);
//    }
@Override
@Transactional
public String closeWallet(CloseWalletRequestPayload payload, String userId, String email) {
    Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
            .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getWalletId()));

    if (!wallet.getUserId().equals(userId)) {
        return gson.toJson(new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
    }

    wallet.setStatus(WalletStatus.CLOSED);
    walletRepository.updateUser(wallet);

    SuccessResponse response = new SuccessResponse();
    response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
    response.setResponseMessage("Wallet closed successfully");
    response.setNewBalance(wallet.getBalance());

    return gson.toJson(response);
}

//    @Override
//    public String lockFunds(LockFundsRequestPayload payload, Users users) {
//        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
//                .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getWalletId()));
//
//        if (!wallet.getUsers().getId().equals(users.getId())) {
//            ErrorResponse errorResponse = new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode());
//            return aesService.encrypt(gson.toJson(errorResponse), users.getEcred());
//        }
//        wallet.setStatus(WalletStatus.LOCKED);
//        walletRepository.updateUser(wallet);
//        SuccessResponse response = new SuccessResponse();
//        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//        response.setResponseMessage("Funds locked successfully");
//        response.setNewBalance(wallet.getBalance());
////        return aesService.encrypt(gson.toJson(response), users.getEcred());
//        return gson.toJson(response);
//    }
@Override
@Transactional
public String lockFunds(LockFundsRequestPayload payload, String userId, String email) {
    Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
            .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getWalletId()));

    if (!wallet.getUserId().equals(userId)) {
        return gson.toJson(new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
    }

    wallet.setStatus(WalletStatus.LOCKED);
    walletRepository.updateUser(wallet);

    SuccessResponse response = new SuccessResponse();
    response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
    response.setResponseMessage("Funds locked successfully");
    response.setNewBalance(wallet.getBalance());

    return gson.toJson(response);
}

//    @Override
//    public String unlockFunds(UnlockFundsRequestPayload payload, Users users) {
//        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
//                .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getWalletId()));
//
//        if (!wallet.getUsers().getId().equals(users.getId())) {
//            ErrorResponse errorResponse = new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode());
//            return aesService.encrypt(gson.toJson(errorResponse), users.getEcred());
//        }
//        wallet.setStatus(WalletStatus.ACTIVE);
//        walletRepository.updateUser(wallet);
//        SuccessResponse response = new SuccessResponse();
//        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//        response.setResponseMessage("Funds unlocked successfully");
//        response.setNewBalance(wallet.getBalance());
////        return aesService.encrypt(gson.toJson(response), users.getEcred());
//        return gson.toJson(response);
//    }
@Override
@Transactional
public String unlockFunds(UnlockFundsRequestPayload payload, String userId, String email) {
    Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
            .orElseThrow(() -> new WalletException("Wallet not found: " + payload.getWalletId()));

    if (!wallet.getUserId().equals(userId)) {
        return gson.toJson(new ErrorResponse("Unauthorized access", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
    }

    wallet.setStatus(WalletStatus.ACTIVE);
    walletRepository.updateUser(wallet);

    SuccessResponse response = new SuccessResponse();
    response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
    response.setResponseMessage("Funds unlocked successfully");
    response.setNewBalance(wallet.getBalance());

    return gson.toJson(response);
}

    @Override
    @Transactional
    public void updateWalletBalance(String walletId, double amount) throws WalletException {
        // Retrieve the wallet entity by walletId
        Wallet wallet = getWalletOrThrow(walletId);

        // Calculate new balance
        BigDecimal newBalance = wallet.getBalance().add(BigDecimal.valueOf(amount));

        // For debits, ensure the balance does not go negative
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new WalletException("Insufficient funds: Cannot debit amount, balance would be negative.");
        }

        // Update and persist the wallet
        wallet.setBalance(newBalance);
        walletRepository.updateUser(wallet);
    }




//    @Override
//    @Transactional
//    public Object processWebhookDeposit(FcmbWebhookPayload payload) {
//        PaymentReference paymentRef = paymentReferenceRepository.findByReferenceCode(payload.getReference())
//                .orElseThrow(() -> new WalletException("Payment reference not found: " + payload.getReference()));
//
//        if (paymentRef.getStatus() == ReferenceStatus.PENDING) {
//            throw new WalletException("This payment reference has already been processed.");
//        }
//
//        if (paymentRef.getAmount().compareTo(payload.getAmount()) != 0) {
//            throw new WalletException("Amount mismatch. Expected: " + paymentRef.getAmount() + ", Received: " + payload.getAmount());
//        }
//
//        // ✅ Update wallet balance
//        Wallet wallet = getWalletOrThrow(String.valueOf(paymentRef.getUser()));
//        wallet.setBalance(wallet.getBalance().add(payload.getAmount()));
//        walletRepository.updateUser(wallet);
//
//        // ✅ Create ledger entry
//        LedgerEntry entry = new LedgerEntry();
//        entry.setWalletId(wallet.getWalletId());
//        entry.setTransactionType(TransactionType.CREDIT);
//        entry.setAmount(payload.getAmount());
//        entry.setStatus(TransactionStatus.COMPLETED);
//        entry.setDescription("Deposit via FCMB webhook");
//        ledgerEntryRepository.save(entry);
//
//        // ✅ Mark reference as completed
//        paymentRef.setStatus(ReferenceStatus.COMPLETED);
//        paymentReferenceRepository.save(paymentRef);
//
//        // ✅ Return success
//        SuccessResponse response = new SuccessResponse();
//        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
//        response.setResponseMessage("Deposit processed successfully");
//        response.setNewBalance(wallet.getBalance());
//        return response;
//    }
@Override
@Transactional
public Object processWebhookDeposit(FcmbWebhookPayload payload) {
    PaymentReference paymentRef = paymentReferenceRepository.findByReferenceCode(payload.getReference())
            .orElseThrow(() -> new WalletException("Payment reference not found: " + payload.getReference()));

    if (paymentRef.getStatus() == ReferenceStatus.COMPLETED) {
        throw new WalletException("This payment reference has already been processed.");
    }

    if (paymentRef.getAmount().compareTo(payload.getAmount()) != 0) {
        throw new WalletException("Amount mismatch. Expected: " + paymentRef.getAmount() + ", Received: " + payload.getAmount());
    }

    // ✅ Fetch wallet using user ID from payment reference
    Users user = usersRepository.findById(paymentRef.getUser().getId())
            .orElseThrow(() -> new WalletException("User not found for payment reference"));

    Wallet wallet = walletRepository.findByWalletId(user.getWalletId())
            .orElseThrow(() -> new WalletException("Wallet not found for user: " + user.getUserName()));

    // ✅ Update wallet balance
    wallet.setBalance(wallet.getBalance().add(payload.getAmount()));
    walletRepository.updateUser(wallet);

    // ✅ Create ledger entry
    LedgerEntry entry = new LedgerEntry();
    entry.setWalletId(wallet.getWalletId());
    entry.setTransactionType(TransactionType.CREDIT);
    entry.setAmount(payload.getAmount());
    entry.setStatus(TransactionStatus.COMPLETED);
    entry.setDescription("Deposit via FCMB webhook");
    entry.setReference(payload.getReference());
    ledgerEntryRepository.save(entry);

    // ✅ Mark payment reference as completed
    paymentRef.setStatus(ReferenceStatus.COMPLETED);
    paymentReferenceRepository.save(paymentRef);

    // ✅ Return response
    SuccessResponse response = new SuccessResponse();
    response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
    response.setResponseMessage("Deposit processed successfully");
    response.setNewBalance(wallet.getBalance());
    return response;
}


//    @Transactional
//    public void creditWalletByEmail(String email, BigDecimal amount, String reference) {
//        Users user = usersRepository.findByEmailAddress(email)
//                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
//
//        Wallet wallet = walletRepository.findByWalletId(user.getWalletId())
//                .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + user.getUserName()));
//
//        // ✅ Idempotency check
//        if (ledgerEntryRepository.existsByReference(reference)) {
//            log.warn("⛔ Duplicate transaction detected for reference: {}", reference);
//            return;
//        }
//
//        // ✅ Update wallet balance
//        wallet.setBalance(wallet.getBalance().add(amount));
//        walletRepository.updateUser(wallet);
//
//        // ✅ Save ledger entry
//        LedgerEntry entry = new LedgerEntry();
//        entry.setWalletId(wallet.getWalletId());
//        entry.setTransactionType(TransactionType.CREDIT);
//        entry.setAmount(amount);
//        entry.setStatus(TransactionStatus.COMPLETED);
//        entry.setDescription("Wallet funded via Paystack");
//        entry.setReference(reference);
//        ledgerEntryRepository.save(entry);
//
//        // ✅ Log audit trail
//        auditLogService.logEvent(
//                wallet.getWalletId(),
//                user.getId().toString(),
//                "CREDIT",
//                reference,
//                "Wallet funded via Paystack"
//        );
//    }
@Transactional
public void creditWalletByEmail(String email, BigDecimal amount, String reference) {
    Users user = usersRepository.findByEmailAddress(email)
            .orElseThrow(() -> new RuntimeException("User not found for email: " + email));

    Wallet wallet = walletRepository.findByWalletId(user.getWalletId())
            .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + user.getUserName()));

    if (ledgerEntryRepository.existsByReference(reference)) {
        log.warn("⛔ Duplicate transaction detected for reference: {}", reference);
        return; // Idempotency: prevent double processing
    }

    // ✅ Update balance
    wallet.setBalance(wallet.getBalance().add(amount));
    walletRepository.updateUser(wallet);

    // ✅ Log ledger entry
    LedgerEntry entry = new LedgerEntry();
    entry.setWalletId(wallet.getWalletId());
    entry.setTransactionType(TransactionType.CREDIT);
    entry.setAmount(amount);
    entry.setStatus(TransactionStatus.COMPLETED);
    entry.setDescription("Wallet funded via Paystack");
    entry.setReference(reference);
    ledgerEntryRepository.save(entry);

    // ✅ Log audit trail
    auditLogService.logEvent(
            wallet.getWalletId(),
            user.getId().toString(),
            "CREDIT",
            reference,
            "Wallet funded via Paystack"
    );
}


    @Transactional
    public String confirmBankTransfer(ConfirmBankTransferRequest request) {
        Wallet wallet = walletRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Wallet not found for account number: " + request.getAccountNumber()));

        // Idempotency Check
        boolean alreadyProcessed = ledgerEntryRepository.existsByReference(request.getReference());
        if (alreadyProcessed) {
            return "Duplicate transfer already processed";
        }

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        walletRepository.updateUser(wallet);

        LedgerEntry entry = new LedgerEntry();
        entry.setWalletId(wallet.getWalletId());
        entry.setTransactionType(TransactionType.CREDIT);
        entry.setAmount(request.getAmount());
        entry.setStatus(TransactionStatus.COMPLETED);
        entry.setDescription("Confirmed Bank Transfer");
        entry.setReference(request.getReference());
        ledgerEntryRepository.save(entry);

        return "Bank transfer confirmed and wallet credited successfully.";
    }


}
