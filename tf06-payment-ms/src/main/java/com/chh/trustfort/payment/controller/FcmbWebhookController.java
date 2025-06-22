package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Util.SecureResponseUtil;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.component.Role;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.enums.ReferenceStatus;
import com.chh.trustfort.payment.enums.TransactionStatus;
import com.chh.trustfort.payment.enums.TransactionType;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.LedgerEntry;
import com.chh.trustfort.payment.model.PaymentReference;
import com.chh.trustfort.payment.model.Wallet;
import com.chh.trustfort.payment.payload.FcmbWebhookPayload;
import com.chh.trustfort.payment.payload.OmniResponsePayload;
import com.chh.trustfort.payment.repository.LedgerEntryRepository;
import com.chh.trustfort.payment.repository.PaymentReferenceRepository;
import com.chh.trustfort.payment.repository.UsersRepository;
import com.chh.trustfort.payment.repository.WalletRepository;
import com.chh.trustfort.payment.security.AesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@Slf4j
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(ApiPath.BASE_API + "/webhook")
public class FcmbWebhookController {

    private final PaymentReferenceRepository paymentReferenceRepository;
    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final Gson gson;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = ApiPath.HANDLE_FCMB_WEBHOOK, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleWebhook(@RequestParam String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request =
                requestManager.validateRequest(Role.HANDLE_FCMB_WEBHOOK.getValue(), requestPayload, httpRequest, idToken);

        if (request.isError) {
            OmniResponsePayload errorResponse = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(errorResponse.getResponseCode(), errorResponse.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        try {
            FcmbWebhookPayload payload = objectMapper.readValue(request.payload, FcmbWebhookPayload.class);

            PaymentReference reference = paymentReferenceRepository.findByReferenceCode(payload.getReference())
                    .orElse(null);

            if (reference == null || !ReferenceStatus.PENDING.equals(reference.getStatus())) {
                log.warn("⚠️ Reference not found or already used: {}", payload.getReference());
                return ResponseEntity.ok(aesService.encrypt("Ignored: Invalid or used reference",(request.appUser)));
            }

            if (payload.getAmount().compareTo(reference.getAmount()) < 0) {
                log.warn("⚠️ FCMB amount mismatch for reference: {}", payload.getReference());
                return ResponseEntity.badRequest().body(aesService.encrypt("Amount mismatch", (request.appUser)));
            }

            Wallet wallet = walletRepository.findByWalletId(String.valueOf(reference.getId()))
                    .orElse(null);

            if (wallet == null) {
                log.error("❌ Wallet not found for user reference ID: {}", reference.getId());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(aesService.encrypt("Wallet not found", (request.appUser)));
            }

            wallet.setBalance(wallet.getBalance().add(payload.getAmount()));
            walletRepository.updateUser(wallet);

            LedgerEntry ledger = new LedgerEntry();
            ledger.setWalletId(wallet.getWalletId());
            ledger.setAmount(payload.getAmount());
            ledger.setTransactionType(TransactionType.CREDIT);
            ledger.setDescription("FCMB Deposit via Virtual Reference");
            ledger.setStatus(TransactionStatus.COMPLETED);
            ledgerEntryRepository.save(ledger);

            reference.setStatus(ReferenceStatus.USED);
            reference.setUsedAt(LocalDateTime.now());
            paymentReferenceRepository.save(reference);

            log.info("✅ Wallet credited (walletId={}): referenceCode={}", wallet.getWalletId(), reference.getReferenceCode());
            return ResponseEntity.ok(aesService.encrypt("Deposit processed successfully", (request.appUser)));

        } catch (Exception e) {
            log.error("❌ Webhook processing error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(aesService.encrypt("Webhook processing error: " + e.getMessage(), (request.appUser)));
        }
    }
}
