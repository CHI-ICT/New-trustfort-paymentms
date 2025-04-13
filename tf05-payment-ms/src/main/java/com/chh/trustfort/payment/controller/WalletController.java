package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Responses.ErrorResponse;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.component.ResponseCode;
import com.chh.trustfort.payment.component.Role;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.exception.WalletException;
import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.payload.*;
import com.chh.trustfort.payment.service.WalletService;
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
@RequestMapping(ApiPath.BASE_API )
public class WalletController {

    private static final Logger log = LoggerFactory.getLogger(WalletController.class);

    @Autowired
    RequestManager requestManager;

    @Autowired
    Gson gson;

    @Autowired
    WalletService walletService;


    /*
        Create Wallet API
     */
    @PostMapping(value = ApiPath.CREATE_WALLET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processCreateWalletRequest(@RequestBody CreateWalletRequestPayload payload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, Users, String> request = requestManager.validateRequest(Role.CREATE_WALLET.getValue(), gson.toJson(payload)
                , httpRequest, ApiPath.ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        // Debugging log
        if (request.Users == null) {
            log.error("Request validation failed: AppUser is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: User not found");
        } else {
            log.info("Authenticated user: " + request.Users.getUserName());
        }

        CreateWalletRequestPayload oCreateWalletRequestPayload = gson.fromJson(request.payload, CreateWalletRequestPayload.class);
        Object response = walletService.createWallet(oCreateWalletRequestPayload, request.Users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Fund Wallet API
     */
    @PostMapping(value = ApiPath.FUND_WALLET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processFundWalletRequest(@RequestBody FundWalletRequestPayload payload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, Users, String> request = requestManager.validateRequest(Role.FUND_WALLET.getValue(), gson.toJson(payload)
                , httpRequest, ApiPath.ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        FundWalletRequestPayload oFundWalletRequestPayload = gson.fromJson(request.payload, FundWalletRequestPayload.class);
        Object response = walletService.fundWallet(oFundWalletRequestPayload, request.Users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Fetch Wallet API
     */
    @GetMapping(value = ApiPath.FETCH_WALLET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processFetchWalletRequest(@RequestParam("walletId") String walletId, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, Users, String> request = requestManager.validateRequest(Role.FETCH_WALLET.getValue(), walletId, httpRequest, ApiPath.ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        Object response = walletService.fetchWallet(walletId, request.Users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Funds Transfer API
     */
    @PostMapping(value = ApiPath.TRANSFER_FUNDS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processFundsTransferRequest(@RequestBody FundsTransferRequestPayload payload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, Users, String> request = requestManager.validateRequest(Role.TRANSFER_FUNDS.getValue(), gson.toJson(payload)
                , httpRequest, ApiPath.ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        FundsTransferRequestPayload oFundsTransferRequestPayload = gson.fromJson(request.payload, FundsTransferRequestPayload.class);
        Object response = walletService.transferFunds(oFundsTransferRequestPayload, request.Users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Check Wallet Balance API
        Fetches the current balance of a given wallet.
     */
    @GetMapping(value = ApiPath.CHECK_BALANCE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processCheckWalletBalanceRequest(@RequestParam("walletId") String walletId, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, Users, String> request =
                requestManager.validateRequest(Role.CHECK_BALANCE.getValue(), walletId, httpRequest, ApiPath.ID_TOKEN);

        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.BAD_REQUEST);
        }

        try {
            Object response = walletService.getWalletBalance(walletId, request.Users);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (WalletException e) {
            return new ResponseEntity<>(new ErrorResponse(ResponseCode.RECORD_NOT_EXIST.getResponseCode(), e.getMessage()), HttpStatus.NOT_FOUND);
        }
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

        Quintuple<Boolean, String, String, Users, String> request = requestManager.validateRequest(Role.TRANSACTION_HISTORY.getValue(), walletId, httpRequest, ApiPath.ID_TOKEN);

        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        Object response = walletService.getTransactionHistory(walletId, startDate, endDate, request.Users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Wallet Withdrawal API
        Allows users to withdraw money from their wallet.
     */
    @PostMapping(value = ApiPath.WITHDRAW_FUNDS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processWithdrawFundsRequest(@RequestBody WithdrawFundsRequestPayload payload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, Users, String> request = requestManager.validateRequest(Role.WITHDRAW_FUNDS.getValue(), gson.toJson(payload)
                , httpRequest, ApiPath.ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        WithdrawFundsRequestPayload oWithdrawFundsRequestPayload = gson.fromJson(request.payload, WithdrawFundsRequestPayload.class);
        Object response = walletService.withdrawFunds(oWithdrawFundsRequestPayload, request.Users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Freeze Wallet  API
        Temporarily blocks transactions on a wallet.
     */
    @PostMapping(value = ApiPath.FREEZE_WALLET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processFreezeWalletRequest(@RequestBody FreezeWalletRequestPayload payload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, Users, String> request = requestManager.validateRequest(Role.FREEZE_WALLET.getValue(), gson.toJson(payload)
                , httpRequest, ApiPath.ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        FreezeWalletRequestPayload oFreezeWalletRequestPayload = gson.fromJson(request.payload, FreezeWalletRequestPayload.class);
        Object response = walletService.freezeWallet(oFreezeWalletRequestPayload, request.Users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Unfreeze Wallet  API
        Restores a wallet to active status, allowing transactions.
     */
    @PostMapping(value = ApiPath.UNFREEZE_WALLET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processUnfreezeWalletRequest(@RequestBody UnfreezeWalletRequestPayload payload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, Users, String> request = requestManager.validateRequest(Role.UNFREEZE_WALLET.getValue(), gson.toJson(payload)
                , httpRequest, ApiPath.ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        UnfreezeWalletRequestPayload oUnfreezeWalletRequestPayload = gson.fromJson(request.payload, UnfreezeWalletRequestPayload.class);
        Object response = walletService.unfreezeWallet(oUnfreezeWalletRequestPayload, request.Users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Close Wallet  API
        Permanently deactivates a wallet.
     */
    @PostMapping(value = ApiPath.CLOSE_WALLET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processCloseWalletRequest(@RequestBody CloseWalletRequestPayload payload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, Users, String> request = requestManager.validateRequest(Role.CLOSE_WALLET.getValue(), gson.toJson(payload)
                , httpRequest, ApiPath.ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        CloseWalletRequestPayload oCloseWalletRequestPayload = gson.fromJson(request.payload, CloseWalletRequestPayload.class);
        Object response = walletService.closeWallet(oCloseWalletRequestPayload, request.Users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Lock Funds API
        Locks a specific amount in a user's wallet, preventing it from being spent or transferred.
     */
    @PostMapping(value = ApiPath.LOCK_FUNDS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processLockFundsRequest(@RequestBody LockFundsRequestPayload payload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, Users, String> request = requestManager.validateRequest(Role.LOCK_FUNDS.getValue(), gson.toJson(payload)
                , httpRequest, ApiPath.ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        LockFundsRequestPayload oLockFundsRequestPayload = gson.fromJson(request.payload, LockFundsRequestPayload.class);
        Object response = walletService.lockFunds(oLockFundsRequestPayload, request.Users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        Unlock Funds API
        Releases previously locked funds, making them available for use.
     */
    @PostMapping(value = ApiPath.UNLOCK_FUNDS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processUnlockFundsRequest(@RequestBody UnlockFundsRequestPayload payload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, Users, String> request = requestManager.validateRequest(Role.UNLOCK_FUNDS.getValue(), gson.toJson(payload)
                , httpRequest, ApiPath.ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        UnlockFundsRequestPayload oUnlockFundsRequestPayload = gson.fromJson(request.payload, UnlockFundsRequestPayload.class);
        Object response = walletService.unlockFunds(oUnlockFundsRequestPayload, request.Users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity<String> testApi() {
        return ResponseEntity.ok("Wallet API is working");
    }

    @PostMapping(value = ApiPath.FUND_WEBHOOK, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processWebhookFromFcmb(@RequestBody FcmbWebhookPayload payload) {
        try {
            Object response = walletService.processWebhookDeposit(payload);
            return ResponseEntity.ok(response);
        } catch (WalletException e) {
            return new ResponseEntity<>(new ErrorResponse(ResponseCode.FAILED_TRANSACTION.getResponseCode(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }



}
