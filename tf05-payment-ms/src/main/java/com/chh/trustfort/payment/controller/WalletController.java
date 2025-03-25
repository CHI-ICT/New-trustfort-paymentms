package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.component.Role;
import com.chh.trustfort.payment.constant.ApiPath;
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
import com.chh.trustfort.payment.service.WalletService;
import com.chh.trustfort.payment.service.WalletServiceImpl;
import com.google.gson.Gson;
import java.time.LocalDate;
import javax.servlet.http.HttpServletRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(ApiPath.BASE_API)
public class WalletController {

    private static final Logger log = LoggerFactory.getLogger(WalletController.class);

    @Autowired
    RequestManager requestManager;

    @Autowired
    Gson gson;

    @Autowired
    WalletService walletService;

    private final String ID_TOKEN = null;

    /*
        Create Wallet API
     */
    @PostMapping(value = ApiPath.CREATE_WALLET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processCreateWalletRequest(@RequestBody String requestPayload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.CFREATE_WALLET.getValue(), requestPayload, httpRequest, ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        // Debugging log
        if (request.appUser == null) {
            log.error("Request validation failed: AppUser is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: User not found");
        } else {
            log.info("Authenticated user: " + request.appUser.getUserName());
        }

        CreateWalletRequestPayload oCreateWalletRequestPayload = gson.fromJson(request.payload, CreateWalletRequestPayload.class);
        Object response = walletService.createWallet(oCreateWalletRequestPayload, request.appUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Fund Wallet API
     */
    @PostMapping(value = ApiPath.FUND_WALLET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processFundWalletRequest(@RequestBody String requestPayload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.FUND_WALLET.getValue(), requestPayload, httpRequest, ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        FundWalletRequestPayload oFundWalletRequestPayload = gson.fromJson(request.payload, FundWalletRequestPayload.class);
        Object response = walletService.fundWallet(oFundWalletRequestPayload, request.appUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Fetch Wallet API
     */
    @GetMapping(value = ApiPath.FETCH_WALLET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processFetchWalletRequest(@RequestParam("walletId") String walletId, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.FETCH_WALLET.getValue(), walletId, httpRequest, ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        Object response = walletService.fetchWallet(walletId, request.appUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Funds Transfer API
     */
    @PostMapping(value = ApiPath.TRANSFER_FUNDS, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processFundsTransferRequest(@RequestBody String requestPayload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.TRANSFER_FUNDS.getValue(), requestPayload, httpRequest, ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        FundsTransferRequestPayload oFundsTransferRequestPayload = gson.fromJson(request.payload, FundsTransferRequestPayload.class);
        Object response = walletService.transferFunds(oFundsTransferRequestPayload, request.appUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Check Wallet Balance API
        Fetches the current balance of a given wallet.
     */
    @GetMapping(value = ApiPath.CHECK_BALANCE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processCheckWalletBalanceRequest(@RequestParam("walletId") String walletId, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.CHECK_BALANCE.getValue(), walletId, httpRequest, ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        Object response = walletService.getWalletBalance(walletId, request.appUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Transaction History API
        Retrieves the list of transactions made from the wallet.
     */
    @GetMapping(value = ApiPath.TRANSACTION_HISTORY, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processTransactionHistoryRequest(
            @RequestParam("walletId") String walletId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.TRANSACTION_HISTORY.getValue(), walletId, httpRequest, ID_TOKEN);

        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        Object response = walletService.getTransactionHistory(walletId, startDate, endDate, request.appUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Wallet Withdrawal API
        Allows users to withdraw money from their wallet.
     */
    @PostMapping(value = ApiPath.WITHDRAW_FUNDS, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processWithdrawFundsRequest(@RequestBody String requestPayload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.WITHDRAW_FUNDS.getValue(), requestPayload, httpRequest, ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        WithdrawFundsRequestPayload oWithdrawFundsRequestPayload = gson.fromJson(request.payload, WithdrawFundsRequestPayload.class);
        Object response = walletService.withdrawFunds(oWithdrawFundsRequestPayload, request.appUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Freeze Wallet  API
        Temporarily blocks transactions on a wallet.
     */
    @PostMapping(value = ApiPath.FREEZE_WALLET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processFreezeWalletRequest(@RequestBody String requestPayload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.FREEZE_WALLET.getValue(), requestPayload, httpRequest, ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        FreezeWalletRequestPayload oFreezeWalletRequestPayload = gson.fromJson(request.payload, FreezeWalletRequestPayload.class);
        Object response = walletService.freezeWallet(oFreezeWalletRequestPayload, request.appUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Unfreeze Wallet  API
        Restores a wallet to active status, allowing transactions.
     */
    @PostMapping(value = ApiPath.UNFREEZE_WALLET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processUnfreezeWalletRequest(@RequestBody String requestPayload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.UNFREEZE_WALLET.getValue(), requestPayload, httpRequest, ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        UnfreezeWalletRequestPayload oUnfreezeWalletRequestPayload = gson.fromJson(request.payload, UnfreezeWalletRequestPayload.class);
        Object response = walletService.unfreezeWallet(oUnfreezeWalletRequestPayload, request.appUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Close Wallet  API
        Permanently deactivates a wallet.
     */
    @PostMapping(value = ApiPath.CLOSE_WALLET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processCloseWalletRequest(@RequestBody String requestPayload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.CLOSE_WALLET.getValue(), requestPayload, httpRequest, ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        CloseWalletRequestPayload oCloseWalletRequestPayload = gson.fromJson(request.payload, CloseWalletRequestPayload.class);
        Object response = walletService.closeWallet(oCloseWalletRequestPayload, request.appUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Lock Funds API
        Locks a specific amount in a user's wallet, preventing it from being spent or transferred.
     */
    @PostMapping(value = ApiPath.LOCK_FUNDS, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processLockFundsRequest(@RequestBody String requestPayload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.LOCK_FUNDS.getValue(), requestPayload, httpRequest, ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        LockFundsRequestPayload oLockFundsRequestPayload = gson.fromJson(request.payload, LockFundsRequestPayload.class);
        Object response = walletService.lockFunds(oLockFundsRequestPayload, request.appUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Unlock Funds API
        Releases previously locked funds, making them available for use.
     */
    @PostMapping(value = ApiPath.UNLOCK_FUNDS, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processUnlockFundsRequest(@RequestBody String requestPayload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.UNLOCK_FUNDS.getValue(), requestPayload, httpRequest, ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        UnlockFundsRequestPayload oUnlockFundsRequestPayload = gson.fromJson(request.payload, UnlockFundsRequestPayload.class);
        Object response = walletService.unlockFunds(oUnlockFundsRequestPayload, request.appUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity<String> testApi() {
        return ResponseEntity.ok("Wallet API is working");
    }


}
