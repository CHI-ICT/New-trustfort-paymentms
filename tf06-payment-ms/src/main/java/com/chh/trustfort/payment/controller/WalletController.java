package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Responses.ErrorResponse;
import com.chh.trustfort.payment.Responses.WalletBalanceResponse;
import com.chh.trustfort.payment.Util.SecureResponseUtil;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.component.ResponseCode;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.LedgerEntryDTO;
import com.chh.trustfort.payment.enums.Role;
import com.chh.trustfort.payment.exception.WalletException;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.LedgerEntry;
import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.model.Wallet;
import com.chh.trustfort.payment.payload.*;
import com.chh.trustfort.payment.repository.UsersRepository;
import com.chh.trustfort.payment.repository.WalletRepository;
import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.WalletService;
import com.google.gson.Gson;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author dofoleta
 */
@RestController
@RefreshScope
@Tag(name = "Wallet API", description = "Handles wallet operations")
@RequiredArgsConstructor
public class WalletController {

    private static final Logger log = LoggerFactory.getLogger(WalletController.class);

    private final RequestManager requestManager;
    private final Gson gson;
    private final WalletService walletService;
    private final WalletRepository walletRepository;
    private final UsersRepository usersRepository;
    private final AesService aesService;




    private static final String KEY_WITH_IV = "99A47258y83921B1627495826M729361/1234567890123456";

//    private UsersData extractUserFromToken(HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader == null || authHeader.isBlank()) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization header");
//        }
//
//        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
//
//        try {
//            String decrypted = aesService.decrypt(token, KEY_WITH_IV);
//            return gson.fromJson(decrypted, UsersData.class);
//        } catch (Exception e) {
//            log.error("Token decryption failed", e);
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
//        }
//    }

    @GetMapping(value = ApiPath.GET_ALL_WALLETS_BY_USER_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getWalletsByPhoneNumber(
            @RequestParam String phoneNumber,
            @RequestHeader("Authorization") String idToken,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.GET_ALL_WALLETS_BY_USER_ID.getValue(),
                null,
                httpRequest,
                idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(
                    aesService.decrypt(request.payload, request.appUser),
                    OmniResponsePayload.class
            );

            return new ResponseEntity<>(
                    aesService.encrypt(SecureResponseUtil.error(
                            response.getResponseMessage(),
                            response.getResponseCode(),
                            "UNAUTHORIZED"
                    ), request.appUser),
                    HttpStatus.OK
            );
        }

        log.info("🔍 Fetching wallets using userId (phone number): {}", phoneNumber);
        String encryptedResponse = walletService.getWalletsByPhoneNumber(phoneNumber, request.appUser);
        return new ResponseEntity<>(encryptedResponse, HttpStatus.OK);
    }

    @PostMapping(value = ApiPath.CREATE_WALLET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createWallet(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        String idToken = authorizationHeader.replace("Bearer ", "").trim();
        log.info("🔐 ID TOKEN: {}", idToken);
        log.info("📥 RAW PAYLOAD (Base64): {}", requestPayload);

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.CREATE_WALLET.getValue(), requestPayload, httpRequest, idToken
        );
        // ✅ Set IP address manually since RequestManager doesn't do it
        request.appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            String decryptedError = aesService.decrypt(request.payload, request.appUser);
            OmniResponsePayload response = gson.fromJson(decryptedError, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        log.info("📥 Decrypted Payload: {}", request.payload);
        CreateWalletRequestPayload decryptedPayload = gson.fromJson(request.payload, CreateWalletRequestPayload.class);
        String result = walletService.createWallet(decryptedPayload, request.appUser);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = ApiPath.FUND_WALLET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> fundWallet(@RequestParam String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.FUND_WALLET.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        FundWalletRequestPayload decryptedPayload = gson.fromJson(request.payload, FundWalletRequestPayload.class);
        String result = walletService.fundWallet(decryptedPayload, String.valueOf(request.appUser.getId()), request.appUser.getEmail());
        return new ResponseEntity<>(aesService.encrypt(result, request.appUser), HttpStatus.OK);
    }


    @GetMapping(value = ApiPath.FETCH_ALL_WALLETS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> fetchAllWallets(HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.FETCH_WALLET.getValue(),
                "", // No payload needed
                httpRequest,
                ApiPath.ID_TOKEN
        );

        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        AppUser user = request.appUser;

        String encryptedResponse = walletService.fetchAllWallets(String.valueOf(user.getId()), user);
        return new ResponseEntity<>(encryptedResponse, HttpStatus.OK);
    }


    @PostMapping(value = ApiPath.TRANSFER_FUNDS, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> transferFunds(@RequestBody String payload, HttpServletRequest httpRequest, @RequestHeader("idToken") String idToken) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.TRANSFER_FUNDS.getValue(), payload, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload errorResponse = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(SecureResponseUtil.error(
                    errorResponse.getResponseCode(), errorResponse.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)
            ), HttpStatus.OK);
        }

        FundsTransferRequestPayload transferPayload = gson.fromJson(request.payload, FundsTransferRequestPayload.class);

        String encryptedResponse = walletService.transferFunds(
                transferPayload,
                idToken,
                request.appUser,
                request.appUser
        );

        return ResponseEntity.ok(encryptedResponse);
    }

    @GetMapping(value = ApiPath.CHECK_BALANCE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkWalletBalanceByPhoneNumber(
            @RequestParam String phoneNumber,
            @RequestHeader("Authorization") String idToken,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.CHECK_BALANCE.getValue(),
                null,
                httpRequest,
                idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(
                    aesService.decrypt(request.payload, request.appUser),
                    OmniResponsePayload.class
            );

            return ResponseEntity.ok(
                    aesService.encrypt(SecureResponseUtil.error(
                            response.getResponseMessage(),
                            response.getResponseCode(),
                            "UNAUTHORIZED"
                    ), request.appUser)
            );
        }

        log.info("📦 Checking wallet balance for phoneNumber: {}", phoneNumber);
        String encryptedResponse = walletService.checkBalanceByPhoneNumber(phoneNumber, request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }


//    @GetMapping(value = ApiPath.TRANSACTION_HISTORY, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> processTransactionHistoryRequest(
//            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
//            @RequestParam("phoneNumber") String phoneNumber,
//            @RequestHeader("Authorization") String jwtToken,
//            HttpServletRequest httpRequest) {
//
//        // ✅ Validate using JWT token (no idToken or payload involved)
//        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
//                Role.TRANSACTION_HISTORY.getValue(), null, httpRequest, jwtToken
//        );
//
//        if (request.isError || request.appUser == null) {
//            return ResponseEntity.ok(aesService.encrypt(gson.toJson(
//                    new ErrorResponse("06", "Unauthorized request")
//            ), null));
//        }
//
//        AppUser appUser = request.appUser;
//
//        log.info("🔍 PhoneNumber passed in request param: {}", phoneNumber);
//
//        // 🔍 Find wallet by phone number (stored as userId)
//        Optional<Wallet> walletOpt = walletRepository.findByUserId(phoneNumber).stream().findFirst();
//
//        if (!walletOpt.isPresent()) {
//            log.warn("❌ No wallet found for phoneNumber: {}", phoneNumber);
//            return ResponseEntity.ok(aesService.encrypt(gson.toJson(
//                    new ErrorResponse("06", "Wallet not found for user")
//            ), appUser));
//        }
//
//        String walletId = walletOpt.get().getWalletId();
//
//        try {
//            List<LedgerEntry> transactions = walletService.getTransactionHistory(walletId, startDate, endDate, phoneNumber).getBody();
//            return ResponseEntity.ok(aesService.encrypt(gson.toJson(transactions), appUser));
//        } catch (WalletException e) {
//            return ResponseEntity.ok(aesService.encrypt(gson.toJson(
//                    new ErrorResponse("06", e.getMessage())
//            ), appUser));
//        } catch (Exception e) {
//            log.error("❌ Error retrieving transaction history: {}", e.getMessage(), e);
//            return ResponseEntity.ok(aesService.encrypt(gson.toJson(
//                    new ErrorResponse("06", "Internal server error")
//            ), appUser));
//        }
//    }
@GetMapping(value = ApiPath.TRANSACTION_HISTORY, produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<?> processTransactionHistoryRequest(
        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam("phoneNumber") String phoneNumber,
        @RequestHeader("Authorization") String jwtToken,
        HttpServletRequest httpRequest) {

    Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
            Role.TRANSACTION_HISTORY.getValue(), null, httpRequest, jwtToken
    );

    if (request.isError || request.appUser == null) {
        return ResponseEntity.ok(aesService.encrypt(gson.toJson(
                new ErrorResponse("06", "Unauthorized request")), null));
    }

    AppUser appUser = request.appUser;
    log.info("🔍 PhoneNumber passed in request param: {}", phoneNumber);

    Optional<Wallet> walletOpt = walletRepository.findByUserId(phoneNumber).stream().findFirst();
    if (!walletOpt.isPresent()) {
        log.warn("❌ No wallet found for phoneNumber: {}", phoneNumber);
        return ResponseEntity.ok(aesService.encrypt(gson.toJson(
                new ErrorResponse("06", "Wallet not found for user")), appUser));
    }

    String walletId = walletOpt.get().getWalletId();

    try {
        List<LedgerEntryDTO> transactions = walletService.getTransactionHistory(walletId, startDate, endDate, phoneNumber).getBody();
        return ResponseEntity.ok(aesService.encrypt(gson.toJson(transactions), appUser));
    } catch (WalletException e) {
        return ResponseEntity.ok(aesService.encrypt(gson.toJson(
                new ErrorResponse("06", e.getMessage())), appUser));
    } catch (Exception e) {
        log.error("❌ Error retrieving transaction history: {}", e.getMessage(), e);
        return ResponseEntity.ok(aesService.encrypt(gson.toJson(
                new ErrorResponse("06", "Internal server error")), appUser));
    }
}



    @PostMapping(value = "/withdraw", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> withdrawFunds(@RequestBody String payload, HttpServletRequest httpRequest, @RequestHeader("idToken") String idToken) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.WITHDRAW_FUNDS.getValue(), payload, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload error = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.ok(SecureResponseUtil.error(error.getResponseCode(), error.getResponseMessage(), "400"));
        }

        WithdrawFundsRequestPayload withdrawPayload = gson.fromJson(request.payload, WithdrawFundsRequestPayload.class);

        String encryptedResponse = walletService.withdrawFunds(
                withdrawPayload,
                String.valueOf(request.appUser.getId()),
                request.appUser.getEmail(),
                idToken,
                request.appUser
        );

        return ResponseEntity.ok(encryptedResponse);
    }


    @PostMapping(value = ApiPath.FREEZE_WALLET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> freezeWallet(
            @RequestHeader("Authorization") String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

    Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
            Role.FREEZE_WALLET.getValue(),
            requestPayload,
            httpRequest,
            idToken
    );

    if (request.isError) {
        OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
        return new ResponseEntity<>(
                SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                HttpStatus.OK
        );
    }

    String serviceResponse = walletService.freezeWallet(request.payload, idToken, request.appUser);
    return new ResponseEntity<>(aesService.encrypt(serviceResponse, request.appUser), HttpStatus.OK);
}


    @PostMapping(value = ApiPath.UNFREEZE_WALLET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processUnfreezeWalletRequest(
            @RequestHeader("Authorization") String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.UNFREEZE_WALLET.getValue(), requestPayload, httpRequest, idToken);

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(SecureResponseUtil.error(
                    response.getResponseCode(),
                    response.getResponseMessage(),
                    String.valueOf(HttpStatus.BAD_REQUEST)), HttpStatus.OK);
        }

        UnfreezeWalletRequestPayload payload = gson.fromJson(request.payload, UnfreezeWalletRequestPayload.class);
        String serviceResponse = walletService.unfreezeWallet(payload, idToken, request.appUser);

        return new ResponseEntity<>(aesService.encrypt(serviceResponse, request.appUser), HttpStatus.OK);
    }


    @PostMapping(value = ApiPath.CLOSE_WALLET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processCloseWalletRequest(
            @RequestHeader("Authorization") String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.CLOSE_WALLET.getValue(), requestPayload, httpRequest, idToken);

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(SecureResponseUtil.error(
                    response.getResponseCode(),
                    response.getResponseMessage(),
                    String.valueOf(HttpStatus.BAD_REQUEST)), HttpStatus.OK);
        }

        CloseWalletRequestPayload payload = gson.fromJson(request.payload, CloseWalletRequestPayload.class);
        String serviceResponse = walletService.closeWallet(payload, idToken, request.appUser);

        return new ResponseEntity<>(aesService.encrypt(serviceResponse, request.appUser), HttpStatus.OK);
    }


    @PostMapping(value = ApiPath.LOCK_FUNDS, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processLockFundsRequest(
            @RequestHeader("Authorization") String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.LOCK_FUNDS.getValue(), requestPayload, httpRequest, idToken);

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(SecureResponseUtil.error(
                    response.getResponseCode(),
                    response.getResponseMessage(),
                    String.valueOf(HttpStatus.BAD_REQUEST)), HttpStatus.OK);
        }

        LockFundsRequestPayload payload = gson.fromJson(request.payload, LockFundsRequestPayload.class);
        String serviceResponse = walletService.lockFunds(payload, idToken, request.appUser);

        return new ResponseEntity<>(aesService.encrypt(serviceResponse, request.appUser), HttpStatus.OK);
    }


    @PostMapping(value = ApiPath.UNLOCK_FUNDS, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processUnlockFundsRequest(
            @RequestHeader("Authorization") String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.UNLOCK_FUNDS.getValue(), requestPayload, httpRequest, idToken);

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(SecureResponseUtil.error(
                    response.getResponseCode(),
                    response.getResponseMessage(),
                    String.valueOf(HttpStatus.BAD_REQUEST)), HttpStatus.OK);
        }

        UnlockFundsRequestPayload payload = gson.fromJson(request.payload, UnlockFundsRequestPayload.class);
        String serviceResponse = walletService.unlockFunds(payload, idToken, request.appUser);

        return new ResponseEntity<>(aesService.encrypt(serviceResponse, request.appUser), HttpStatus.OK);
    }


    @PostMapping(value = ApiPath.UPDATE_WALLET_BALANCE, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processUpdateWalletBalanceRequest(
            @RequestHeader("Authorization") String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.UPDATE_WALLET_BALANCE.getValue(), requestPayload, httpRequest, idToken);

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(SecureResponseUtil.error(
                    response.getResponseCode(),
                    response.getResponseMessage(),
                    String.valueOf(HttpStatus.BAD_REQUEST)), HttpStatus.OK);
        }

        UpdateWalletBalancePayload payload = gson.fromJson(request.payload, UpdateWalletBalancePayload.class);
        String response = walletService.updateWalletBalance(payload, idToken, request.appUser);

        return new ResponseEntity<>(aesService.encrypt(response, request.appUser), HttpStatus.OK);
    }

    @PostMapping(value = ApiPath.FUND_WEBHOOK, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processWebhookFromFcmb(@RequestParam String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.PROCESS_WEBHOOK.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        FcmbWebhookPayload decryptedPayload = gson.fromJson(request.payload, FcmbWebhookPayload.class);
        String result = (String) walletService.processWebhookDeposit(decryptedPayload, idToken, request.appUser);
        return new ResponseEntity<>(aesService.encrypt(result, request.appUser), HttpStatus.OK);
    }

    @PostMapping(value = ApiPath.CREDIT_WALLET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> creditWalletByEmail(@RequestParam String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {

        // 🔐 Step 1: Validate and decrypt the request
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.CREDIT_WALLET.getValue(), requestPayload, httpRequest, idToken
        );

        // ❌ Step 2: Handle validation failure
        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        // ✅ Step 3: Parse decrypted payload into the expected DTO
        CreditWalletRequestPayload decryptedPayload = gson.fromJson(request.payload, CreditWalletRequestPayload.class);

        // ⚙️ Step 4: Invoke service with raw parameters
        walletService.creditWalletByEmail(
                decryptedPayload.getEmail(),
                decryptedPayload.getAmount(),
                decryptedPayload.getReference()
        );

        // 🔒 Step 5: Return encrypted success response
        String responseMessage = "Wallet credited successfully";
        return new ResponseEntity<>(aesService.encrypt(responseMessage, request.appUser), HttpStatus.OK);
    }




}
