package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Responses.ConfirmBankTransferResponse;
import com.chh.trustfort.payment.Util.SecureResponseUtil;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.enums.Role;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.ConfirmBankTransferRequest;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.payload.OmniResponsePayload;
import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.ServiceImpl.WalletServiceImpl;
import com.chh.trustfort.payment.service.WalletService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import javax.servlet.http.HttpServletRequest;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
public class BankTransferController {

    private final WalletServiceImpl walletService;
    private final RequestManager requestManager;
    private final Gson gson;
    private final AesService aesService;

    @PostMapping(value = ApiPath.CONFIRM_TRANSFER, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> confirmBankTransfer(@RequestHeader(ApiPath.ID_TOKEN) String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.CONFIRM_BANK_TRANSFER.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        ConfirmBankTransferRequest decryptedPayload = gson.fromJson(request.payload, ConfirmBankTransferRequest.class);
        String result = walletService.confirmBankTransfer(decryptedPayload, idToken, request.appUser);
        return new ResponseEntity<>(aesService.encrypt(result, request.appUser), HttpStatus.OK);
    }
}
