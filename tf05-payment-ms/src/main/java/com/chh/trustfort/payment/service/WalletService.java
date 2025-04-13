package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.Responses.WalletBalanceResponse;
import com.chh.trustfort.payment.exception.WalletException;
import com.chh.trustfort.payment.model.LedgerEntry;
import com.chh.trustfort.payment.model.Users;
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

    public String createWallet(CreateWalletRequestPayload payload, Users users);

    public String fundWallet(FundWalletRequestPayload payload, Users users);

    public String fetchWallet(String walletId, Users users);

    public String transferFunds(FundsTransferRequestPayload payload, Users users);
    
    public WalletBalanceResponse getWalletBalance(String walletId, Users users);
    
    public ResponseEntity<List<LedgerEntry>> getTransactionHistory(String walletId, LocalDate startDate, LocalDate endDate, Users users);
    
    public String withdrawFunds(WithdrawFundsRequestPayload payload, Users users);
    
    public String freezeWallet(FreezeWalletRequestPayload payload, Users users);
    
    public String unfreezeWallet(UnfreezeWalletRequestPayload payload, Users users);
    
    public String closeWallet(CloseWalletRequestPayload payload, Users users);
    
    public String lockFunds(LockFundsRequestPayload payload, Users users);
    
    public String unlockFunds(UnlockFundsRequestPayload payload, Users users);

    void updateWalletBalance(String walletId, double amount) throws WalletException;
    Object processWebhookDeposit(FcmbWebhookPayload payload);

    void creditWalletByEmail(String userName, BigDecimal amount, String reference);
}
