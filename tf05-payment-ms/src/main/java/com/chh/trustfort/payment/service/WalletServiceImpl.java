package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.component.ResponseCode;
import com.chh.trustfort.payment.component.WalletUtil;
import com.chh.trustfort.payment.enums.WalletStatus;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.Users;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
    private AppUserService appUserService;

    @Autowired
    private WalletUtil walletUtil;

    @Autowired
    private AesService aesService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private Gson gson;

    @Override
    public String createWallet(@Valid CreateWalletRequestPayload requestPayload, AppUser appUser) {
        log.info("Creating wallet for user ID: {}", requestPayload.getOwner().getId());

        CreateWalletResponsePayload oResponse = new CreateWalletResponsePayload();
        oResponse.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());
        oResponse.setResponseMessage(messageSource.getMessage("failed", null, Locale.ENGLISH));

        // ✅ Extract and validate owner
        Long ownerId = requestPayload.getOwner().getId();
        AppUser owner = appUserService.getUserById(ownerId);

        if (owner == null) {
            log.warn("User not found: {}", ownerId);
            oResponse.setResponseMessage(messageSource.getMessage("user.not.found", null, Locale.ENGLISH));
            return aesService.encrypt(gson.toJson(oResponse), String.valueOf(appUser));
        }

        // ✅ Check if the user already has a wallet
        if (walletRepository.existsByOwner(owner)) {
            log.warn("Wallet already exists for user ID: {}", ownerId);
            oResponse.setResponseMessage(messageSource.getMessage("wallet.already.exists", null, Locale.ENGLISH));
            return aesService.encrypt(gson.toJson(oResponse), appUser.getEcred());
        }

        // Create and persist wallet
        Wallet wallet = new Wallet();
        String generatedId = walletRepository.generateWalletId();
        wallet.setWalletId(generatedId);
        // ***** NEW: Set the serial number so that it’s not null *****
        wallet.setSerialNumber(Long.valueOf(generatedId));
        wallet.setOwner(owner);
        wallet.setCurrency(requestPayload.getCurrency());
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatus(WalletStatus.ACTIVE);
        wallet = walletRepository.createWallet(wallet);

        log.info("Wallet created successfully with ID: {}", wallet.getWalletId());

        // ✅ Validate wallet creation
        if (walletUtil.validateWalletId(wallet.getWalletId())) {
            oResponse.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
            oResponse.setResponseMessage(messageSource.getMessage("wallet.created.success", null, Locale.ENGLISH));
        }


        return aesService.encrypt(gson.toJson(oResponse), appUser.getEcred());
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
