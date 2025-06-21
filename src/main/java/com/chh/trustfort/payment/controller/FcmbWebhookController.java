package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.enums.ReferenceStatus;
import com.chh.trustfort.payment.enums.TransactionStatus;
import com.chh.trustfort.payment.enums.TransactionType;
import com.chh.trustfort.payment.model.LedgerEntry;
import com.chh.trustfort.payment.model.PaymentReference;
import com.chh.trustfort.payment.model.Wallet;
import com.chh.trustfort.payment.payload.FcmbWebhookPayload;
import com.chh.trustfort.payment.repository.LedgerEntryRepository;
import com.chh.trustfort.payment.repository.PaymentReferenceRepository;
import com.chh.trustfort.payment.repository.UsersRepository;
import com.chh.trustfort.payment.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;


@Slf4j

@RestController
@RequestMapping(ApiPath.BASE_API + "/webhook")
public class FcmbWebhookController {

    @Autowired
    private PaymentReferenceRepository paymentReferenceRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private LedgerEntryRepository ledgerEntryRepository;

    @Autowired
    private UsersRepository usersRepository;


    @PostMapping(value = ApiPath.HANDLE_FCMB_WEBHOOK, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handleFcmbWebhook(@RequestBody FcmbWebhookPayload payload) {
        log.info("Received FCMB Webhook for reference: {}", payload.getReference());

        // 1. Fetch the payment reference
        PaymentReference reference = paymentReferenceRepository.findByReferenceCode(payload.getReference())
                .orElse(null);

        if (reference == null || !reference.getStatus().equals(ReferenceStatus.PENDING)) {
            log.warn("Reference not found or already used: {}", payload.getReference());
            return ResponseEntity.ok("Ignored: Invalid or used reference");
        }

        // 2. Validate deposit amount (optional but recommended)
        if (payload.getAmount().compareTo(reference.getAmount()) < 0) {
            log.warn("FCMB amount mismatch for reference: {}", payload.getReference());
            return ResponseEntity.badRequest().body("Amount mismatch");
        }

        // 3. Credit the wallet
        Wallet wallet = walletRepository.findByWalletId(String.valueOf(reference.getId()))
                .orElse(null);

        if (wallet == null) {
            log.error("Wallet not found for user ID: {}", reference.getUser().getId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wallet not found");
        }

        wallet.setBalance(wallet.getBalance().add(payload.getAmount()));
        walletRepository.updateUser(wallet);

        // 4. Record ledger entry
        LedgerEntry ledger = new LedgerEntry();
        ledger.setWalletId(wallet.getWalletId());
        ledger.setAmount(payload.getAmount());
        ledger.setTransactionType(TransactionType.CREDIT);
        ledger.setDescription("FCMB Deposit via Virtual Reference");
        ledger.setStatus(TransactionStatus.COMPLETED);
        ledgerEntryRepository.save(ledger);

        // 5. Mark reference as used
        reference.setStatus(ReferenceStatus.USED);
        reference.setUsedAt(LocalDateTime.now());
        paymentReferenceRepository.save(reference);

        log.info("Successfully credited wallet ID: {} for reference: {}", wallet.getWalletId(), reference.getReferenceCode());
        return ResponseEntity.ok("Deposit processed successfully");
    }




}
