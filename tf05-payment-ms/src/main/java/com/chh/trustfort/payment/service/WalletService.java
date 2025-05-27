package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.Responses.WalletBalanceResponse;
import com.chh.trustfort.payment.exception.WalletException;
import com.chh.trustfort.payment.model.LedgerEntry;
import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.model.Wallet;
import com.chh.trustfort.payment.payload.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author DOfoleta
 */
public interface WalletService {

//    public String createWallet(CreateWalletRequestPayload payload, Users users);
Wallet createWallet(String userId, String emailAddress);

//    public String fundWallet(FundWalletRequestPayload payload, Users users);
String fundWallet(FundWalletRequestPayload payload, String userId, String emailAddress);

//    public String fetchWallet(String walletId, Users users);
String fetchWallet(String walletId, String userId);
//    public String transferFunds(FundsTransferRequestPayload payload, Users users);
public String transferFunds(FundsTransferRequestPayload payload, String userId, String email);

//    public WalletBalanceResponse getWalletBalance(String walletId, Users users);
public WalletBalanceResponse getWalletBalance(String walletId, String userId);

//    public ResponseEntity<List<LedgerEntry>> getTransactionHistory(String walletId, LocalDate startDate, LocalDate endDate, Users users);
public ResponseEntity<List<LedgerEntry>> getTransactionHistory(String walletId, LocalDate startDate, LocalDate endDate, String userId);

//    public String withdrawFunds(WithdrawFundsRequestPayload payload, Users users);
public String withdrawFunds(WithdrawFundsRequestPayload payload, String userId, String email);

//    public String freezeWallet(FreezeWalletRequestPayload payload, Users users);
public String freezeWallet(FreezeWalletRequestPayload payload, String userId, String email);

//    public String unfreezeWallet(UnfreezeWalletRequestPayload payload, Users users);
String unfreezeWallet(UnfreezeWalletRequestPayload payload, String userId, String email);

//    public String closeWallet(CloseWalletRequestPayload payload, Users users);
String closeWallet(CloseWalletRequestPayload payload, String userId, String email);

//    public String lockFunds(LockFundsRequestPayload payload, Users users);
String lockFunds(LockFundsRequestPayload payload, String userId, String email);

//    public String unlockFunds(UnlockFundsRequestPayload payload, Users users);
String unlockFunds(UnlockFundsRequestPayload payload, String userId, String email);


    void updateWalletBalance(String walletId, double amount) throws WalletException;
    Object processWebhookDeposit(FcmbWebhookPayload payload);

    void creditWalletByEmail(String userName, BigDecimal amount, String reference);
}
