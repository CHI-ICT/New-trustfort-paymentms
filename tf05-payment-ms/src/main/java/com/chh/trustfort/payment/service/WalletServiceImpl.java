package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.component.ResponseCode;
import com.chh.trustfort.payment.component.WalletUtil;
import com.chh.trustfort.payment.enums.WalletStatus;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.Wallet;
import com.chh.trustfort.payment.payload.CloseWalletRequestPayload;
import com.chh.trustfort.payment.payload.CreateWalletRequestPayload;
import com.chh.trustfort.payment.payload.CreateWalletResponsePayload;
import com.chh.trustfort.payment.payload.FreezeWalletRequestPayload;
import com.chh.trustfort.payment.payload.FundWalletRequestPayload;
import com.chh.trustfort.payment.payload.FundsTransferRequestPayload;
import com.chh.trustfort.payment.payload.LockFundsRequestPayload;
import com.chh.trustfort.payment.payload.UnfreezeWalletRequestPayload;
import com.chh.trustfort.payment.payload.UnlockFundsRequestPayload;
import com.chh.trustfort.payment.payload.WithdrawFundsRequestPayload;
import com.chh.trustfort.payment.repository.AppUserRepository;
import com.chh.trustfort.payment.repository.WalletRepository;
import com.chh.trustfort.payment.security.AesService;
import com.google.gson.Gson;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

/**
 *
 * @author DOfoleta
 */
@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    WalletUtil walletUtil;

    @Autowired
    private AesService aesService;

//    @Autowired
//    AesService aesService;

//    @Autowired
//    MessageSource messageSource;

    @Autowired
    private MessageSource messageSource;

//    @Autowired
//    Gson gson;

    @Autowired
    private Gson gson;

    @Override
    public String createWallet(@Valid CreateWalletRequestPayload requestPayload, AppUser appUser) {

        CreateWalletResponsePayload oResponse = new CreateWalletResponsePayload();
        oResponse.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());
        oResponse.setResponseMessage(messageSource.getMessage("failed", new Object[0], Locale.ENGLISH));

        // ✅ Extract the ID from Users object
        Long ownerId = requestPayload.getOwner().getId();

        // ✅ Check if user exists
        if (!appUserRepository.userExistsById(ownerId)) {
            oResponse.setResponseMessage(messageSource.getMessage("user.not.found", null, Locale.ENGLISH));
            return aesService.encrypt(gson.toJson(oResponse), appUser);
        }

        // ✅ Check if wallet already exists
        if (walletRepository.existsByOwner(ownerId)) {
            oResponse.setResponseMessage(messageSource.getMessage("wallet.already.exists", null, Locale.ENGLISH));
            return aesService.encrypt(gson.toJson(oResponse), appUser);
        }


        Wallet wallet = new Wallet();
        wallet.setWalletId(walletRepository.generateWalletId());
        wallet.setOwner(requestPayload.getOwner());
        wallet.setCurrency(requestPayload.getCurrency());
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatus(WalletStatus.ACTIVE);

        wallet = walletRepository.createWallet(wallet);

        // Validate wallet creation
        if (walletUtil.validateWalletId(wallet.getWalletId())) {
            oResponse.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
            oResponse.setResponseMessage(messageSource.getMessage("wallet.created.success", null, Locale.ENGLISH));
        }

//        if (walletUtil.validateWalletId(wallet.getWalletId())) {
//            oResponse.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());
//            oResponse.setResponseMessage(messageSource.getMessage("failed", new Object[0], Locale.ENGLISH));
//        }

        return aesService.encrypt(gson.toJson(oResponse), appUser);
    }

    @Override
    public String fundWallet(FundWalletRequestPayload payload, AppUser appUser) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String fetchWallet(String walletId, AppUser appUse) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String transferFunds(FundsTransferRequestPayload payload, AppUser appUser) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getWalletBalance(String walletId, AppUser appUser) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getTransactionHistory(String walletId, LocalDate startDate, LocalDate endDate, AppUser appUser) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String withdrawFunds(WithdrawFundsRequestPayload payload, AppUser appUser) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String freezeWallet(FreezeWalletRequestPayload payload, AppUser appUser) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String unfreezeWallet(UnfreezeWalletRequestPayload payload, AppUser appUser) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String closeWallet(CloseWalletRequestPayload payload, AppUser appUser) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String lockFunds(LockFundsRequestPayload payload, AppUser appUser) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String unlockFunds(UnlockFundsRequestPayload payload, AppUser appUser) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
