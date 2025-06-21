package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.ConfirmBankTransferRequest;
import com.chh.trustfort.payment.dto.GenerateAccountNumberRequest;
import com.chh.trustfort.payment.dto.GenerateAccountNumberResponse;
import com.chh.trustfort.payment.service.VirtualAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPath.BASE_API )
@RequiredArgsConstructor
public class VirtualAccountController {

    private final VirtualAccountService virtualAccountService;

    @PostMapping(value = ApiPath.GENERATE_ACCOUNT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenerateAccountNumberResponse> generateAccount(@RequestBody GenerateAccountNumberRequest request) {
        return ResponseEntity.ok(virtualAccountService.generateAccountNumber(request.getWalletId()));
    }

    @PostMapping(value = ApiPath.CONFIRM_BANK_TRANSFER, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> confirmBankTransfer(@RequestBody ConfirmBankTransferRequest request) {
        String result = virtualAccountService.confirmBankTransfer(request);
        return ResponseEntity.ok(result);
    }

}