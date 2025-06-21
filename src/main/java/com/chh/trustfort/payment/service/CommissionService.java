package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.Responses.ErrorResponse;
import com.chh.trustfort.payment.Responses.SuccessResponse;
import com.chh.trustfort.payment.constant.ResponseCode;
import com.chh.trustfort.payment.dto.WithdrawCommissionRequest;
import com.chh.trustfort.payment.enums.CommissionType;
import com.chh.trustfort.payment.enums.TransactionStatus;
import com.chh.trustfort.payment.enums.TransactionType;
import com.chh.trustfort.payment.exception.WalletException;
import com.chh.trustfort.payment.model.*;
import com.chh.trustfort.payment.repository.*;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Withdrawal successful"),
        @ApiResponse(responseCode = "400", description = "Invalid request or PIN")
})

@Slf4j
@Service
@RequiredArgsConstructor
public class CommissionService {

    private final CommissionRepository commissionRepository;
    private final WalletRepository walletRepository;
    private final UsersRepository usersRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final SettlementAccountRepository settlementAccountRepository;
    private final MockFCMBIntegrationService mockFCMBIntegrationService;
    private final Gson gson;
    private final PinService pinService;
    private final AuditLogService auditLogService;

    public String creditCommission(Long userId, BigDecimal amount, CommissionType commissionType, String reference) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByEmailAddress(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        Commission commission = new Commission();
        commission.setUser(user);
        commission.setAmount(amount);
        commission.setCommissionType(commissionType);
        commission.setReference(reference);
        commission.setStatus(TransactionStatus.PENDING);
        commissionRepository.save(commission);

        // Update wallet balance
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.updateUser(wallet);

        commission.setStatus(TransactionStatus.COMPLETED);
        commissionRepository.save(commission);

        return "Commission credited successfully";
    }

    public List<Commission> getUserCommissions(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return commissionRepository.findByUser(user);
    }


    @Transactional
    public String withdrawCommission(WithdrawCommissionRequest payload, AppUser user) {
        log.info("Processing commission withdrawal for user: {}", user.getUserName());

        // ✅ Step 0: Validate PIN before proceeding
        if (!pinService.matches(payload.getTransactionPin(), user.getTransactionPin())) {
            log.warn("Invalid PIN attempt for commission withdrawal by user {}", user.getUserName());
            return gson.toJson(new ErrorResponse("Invalid transaction PIN", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        // ✅ Step 1: Calculate total COMPLETED commissions
        List<Commission> commissions = commissionRepository.findByUserAndStatus(user, TransactionStatus.COMPLETED);
        BigDecimal totalEarned = commissions.stream()
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalEarned.compareTo(payload.getAmount()) < 0) {
            log.warn("Insufficient earned commissions. Available: {}, Requested: {}", totalEarned, payload.getAmount());
            return gson.toJson(new ErrorResponse("Insufficient commission balance", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        // ✅ Step 2: Fetch wallet
        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
                .orElseThrow(() -> new WalletException("Wallet not found"));

        // ✅ Step 3: Deduct from wallet
        if (wallet.getBalance().compareTo(payload.getAmount()) < 0) {
            return gson.toJson(new ErrorResponse("Insufficient wallet balance", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }
        wallet.setBalance(wallet.getBalance().subtract(payload.getAmount()));
        walletRepository.updateUser(wallet);

        // ✅ Step 4: Log Ledger Entry
        LedgerEntry ledger = new LedgerEntry();
        ledger.setWalletId(wallet.getWalletId());
        ledger.setTransactionType(TransactionType.DEBIT);
        ledger.setAmount(payload.getAmount());
        ledger.setStatus(TransactionStatus.PENDING);
        ledger.setDescription("Commission Withdrawal");
        ledgerEntryRepository.save(ledger);

        // ✅ Step 5: Move funds to settlement account
        String settlementAccountNumber = "FCMB-SETTLEMENT-001";
        SettlementAccount settlementAccount = settlementAccountRepository.findByAccountNumber(settlementAccountNumber);
        if (settlementAccount == null) {
            settlementAccount = new SettlementAccount();
            settlementAccount.setAccountNumber(settlementAccountNumber);
            settlementAccount.setBalance(BigDecimal.ZERO);
        }
        settlementAccount.setBalance(settlementAccount.getBalance().add(payload.getAmount()));
        settlementAccountRepository.save(settlementAccount);

        // ✅ Step 6: Call FCMB mock API
        boolean fcmbSuccess = mockFCMBIntegrationService.transferFunds(settlementAccountNumber, payload.getAmount());
        if (fcmbSuccess) {
            ledger.setStatus(TransactionStatus.COMPLETED);
        } else {
            // rollback wallet debit
            wallet.setBalance(wallet.getBalance().add(payload.getAmount()));
            walletRepository.updateUser(wallet);
            ledger.setStatus(TransactionStatus.FAILED);
        }
        ledgerEntryRepository.save(ledger);
        // ✅ AUDIT LOG - record the withdrawal attempt
        auditLogService.logEvent(
                wallet.getWalletId(),
                user.getId().toString(),
                "DEBIT",
                ledger.getReference() != null ? ledger.getReference() : "N/A",
                fcmbSuccess
                        ? "Commission withdrawal processed"
                        : "Commission withdrawal failed – amount refunded"
        );

        SuccessResponse response = new SuccessResponse();
        response.setResponseCode(ResponseCode.SUCCESS_CODE.getResponseCode());
        response.setResponseMessage(fcmbSuccess
                ? "Commission withdrawal processed successfully"
                : "Withdrawal failed, commission amount refunded to wallet");
        response.setNewBalance(wallet.getBalance());

        return gson.toJson(response);
    }
}

