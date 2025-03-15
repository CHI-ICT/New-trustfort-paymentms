package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.payload.CloseWalletRequestPayload;
import com.chh.trustfort.payment.payload.CreateWalletRequestPayload;
import com.chh.trustfort.payment.payload.FreezeWalletRequestPayload;
import com.chh.trustfort.payment.payload.FundWalletRequestPayload;
import com.chh.trustfort.payment.payload.FundsTransferRequestPayload;
import com.chh.trustfort.payment.payload.LockFundsRequestPayload;
import com.chh.trustfort.payment.payload.UnfreezeWalletRequestPayload;
import com.chh.trustfort.payment.payload.UnlockFundsRequestPayload;
import com.chh.trustfort.payment.payload.WithdrawFundsRequestPayload;
import java.time.LocalDate;

/**
 *
 * @author DOfoleta
 */
public interface WalletService {

    public String createWallet(CreateWalletRequestPayload payload, AppUser appUser);

    public String fundWallet(FundWalletRequestPayload payload,AppUser appUser);

    public String fetchWallet(String walletId,AppUser appUser);

    public String transferFunds(FundsTransferRequestPayload payload,AppUser appUser);
    
    public String getWalletBalance(String walletId,AppUser appUser);
    
    public String getTransactionHistory(String walletId,LocalDate startDate, LocalDate endDate, AppUser appUser);
    
    public String withdrawFunds(WithdrawFundsRequestPayload payload,AppUser appUser);
    
    public String freezeWallet(FreezeWalletRequestPayload payload,AppUser appUser);
    
    public String unfreezeWallet(UnfreezeWalletRequestPayload payload,AppUser appUser);
    
    public String closeWallet(CloseWalletRequestPayload payload,AppUser appUser);
    
    public String lockFunds(LockFundsRequestPayload payload,AppUser appUser);
    
    public String unlockFunds(UnlockFundsRequestPayload payload,AppUser appUser);
}
