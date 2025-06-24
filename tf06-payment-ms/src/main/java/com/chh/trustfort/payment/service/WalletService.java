package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.Responses.WalletBalanceResponse;
import com.chh.trustfort.payment.dto.ConfirmBankTransferRequest;
import com.chh.trustfort.payment.dto.WalletDTO;
import com.chh.trustfort.payment.exception.WalletException;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.LedgerEntry;
import com.chh.trustfort.payment.model.Wallet;
import com.chh.trustfort.payment.payload.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author DOfoleta
 */
public interface WalletService {
    List<WalletDTO> getWalletsByUserId(String userId);

    String checkBalanceByPhoneNumber(String phoneNumber, AppUser appUser);
    String getWalletsByPhoneNumber(String phoneNumber, AppUser appUser);

    String createWallet(CreateWalletRequestPayload requestPayload, AppUser appUser);

String fundWallet(FundWalletRequestPayload payload, String userId, String emailAddress);

String fetchAllWallets(String userId, AppUser user);

public String transferFunds(FundsTransferRequestPayload payload, String idToken, AppUser appUser, AppUser ecred);

public WalletBalanceResponse getWalletBalance(String walletId, String userId);

public ResponseEntity<List<LedgerEntry>> getTransactionHistory(String walletId, LocalDate startDate, LocalDate endDate, String userId);

public String withdrawFunds(WithdrawFundsRequestPayload payload, String userId, String email, String idToken, AppUser appUser);

public String freezeWallet(String requestPayload, String idToken, AppUser appUser);

String unfreezeWallet(UnfreezeWalletRequestPayload payload, String idToken, AppUser appUser);

String closeWallet(CloseWalletRequestPayload payload, String idToken, AppUser appUser);

String lockFunds(LockFundsRequestPayload payload, String idToken, AppUser appUser);

String unlockFunds(UnlockFundsRequestPayload payload, String idToken, AppUser appUser);

String updateWalletBalance(UpdateWalletBalancePayload payload, String idToken, AppUser appUser) throws WalletException;
Object processWebhookDeposit(FcmbWebhookPayload payload, String idToken, AppUser appUser);

void creditWalletByEmail(String email, BigDecimal amount, String reference);

String confirmBankTransfer(ConfirmBankTransferRequest payload, String idToken, AppUser appUser);
}
