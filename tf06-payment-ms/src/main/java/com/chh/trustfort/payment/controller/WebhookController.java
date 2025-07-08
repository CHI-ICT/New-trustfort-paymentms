package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Util.SecureResponseUtil;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.component.Role;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.WebhookPayload;
import com.chh.trustfort.payment.enums.TransactionStatus;
import com.chh.trustfort.payment.exception.WalletException;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.WalletLedgerEntry;
import com.chh.trustfort.payment.model.SettlementAccount;
import com.chh.trustfort.payment.model.Wallet;
import com.chh.trustfort.payment.payload.OmniResponsePayload;
import com.chh.trustfort.payment.repository.LedgerEntryRepository;
import com.chh.trustfort.payment.repository.SettlementAccountRepository;
import com.chh.trustfort.payment.repository.WalletRepository;
import com.chh.trustfort.payment.security.AesService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;


import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
public class WebhookController {

    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final SettlementAccountRepository settlementAccountRepository;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;

    @PostMapping(value = ApiPath.HANDLE_FCMB_WEBHOOK, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleFcmbWebhook(@RequestParam String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {

        // Step 1: Validate and decrypt request
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.HANDLE_WEBHOOK.getValue(), requestPayload, httpRequest, idToken
        );

        // Step 2: Handle validation error
        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        // Step 3: Parse decrypted payload
        WebhookPayload payload = gson.fromJson(request.payload, WebhookPayload.class);
        log.info("📨 Received webhook for wallet {} with status {}", payload.getWalletId(), payload.getTransferStatus());

        // Step 4: Fetch wallet
        Wallet wallet = walletRepository.findByWalletId(payload.getWalletId())
                .orElseThrow(() -> new WalletException("Wallet not found"));

        // Step 5: Fetch pending ledger
        WalletLedgerEntry walletLedgerEntry = ledgerEntryRepository.findPendingByWalletId(wallet.getWalletId())
                .orElseThrow(() -> new WalletException("No pending ledger found"));

        BigDecimal amount = BigDecimal.valueOf(payload.getAmount());

        // Step 6: Process based on transfer status
        if ("SUCCESS".equalsIgnoreCase(payload.getTransferStatus())) {
            walletLedgerEntry.setStatus(TransactionStatus.COMPLETED);
            log.info("✅ Ledger updated to COMPLETED");
        } else {
            wallet.setBalance(wallet.getBalance().add(amount));
            walletRepository.updateUser(wallet);

            walletLedgerEntry.setStatus(TransactionStatus.FAILED);
            log.warn("❌ Transfer failed. Wallet refunded");
        }

        ledgerEntryRepository.save(walletLedgerEntry);

        // Step 7: Update settlement account
        SettlementAccount account = settlementAccountRepository.findByAccountNumber("MOCK-FCMB-001");
        if (account != null) {
            account.setBalance(account.getBalance().subtract(amount));
            settlementAccountRepository.save(account);
        }

        // Step 8: Encrypt and return response
        String result = "Webhook processed";
        return new ResponseEntity<>(aesService.encrypt(result, request.appUser), HttpStatus.OK);
    }
}