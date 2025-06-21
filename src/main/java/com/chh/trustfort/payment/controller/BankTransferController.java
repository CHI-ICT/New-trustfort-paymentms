package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Responses.ConfirmBankTransferResponse;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.ConfirmBankTransferRequest;
import com.chh.trustfort.payment.service.ServiceImpl.WalletServiceImpl;
import com.chh.trustfort.payment.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.BASE_API )
@RequiredArgsConstructor
public class BankTransferController {

    private final WalletServiceImpl walletService;


    @PostMapping(value = ApiPath.CONFIRM_TRANSFER, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> confirmBankTransfer(@RequestBody ConfirmBankTransferRequest request) {
        String response = walletService.confirmBankTransfer(request);
        return ResponseEntity.ok(response);
    }
}