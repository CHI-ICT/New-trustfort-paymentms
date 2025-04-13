package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Responses.VerifyFlutterwaveResponse;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.VerifyFlutterwaveRequest;
import com.chh.trustfort.payment.service.FlutterwaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
public class FlutterwaveController {

    private final FlutterwaveService flutterwaveService;

    @PostMapping(value = ApiPath.VERIFY_FLW_TRANSACTION, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VerifyFlutterwaveResponse> verifyTransaction(@RequestBody VerifyFlutterwaveRequest request) {
        return ResponseEntity.ok(flutterwaveService.verifyTransaction(request));
    }
}
