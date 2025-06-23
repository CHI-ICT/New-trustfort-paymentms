package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Responses.ErrorResponse;
import com.chh.trustfort.payment.Util.SecureResponseUtil;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.component.ResponseCode;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.CommissionRequest;
import com.chh.trustfort.payment.dto.WithdrawCommissionRequest;
import com.chh.trustfort.payment.enums.CommissionType;
import com.chh.trustfort.payment.exception.WalletException;
import com.chh.trustfort.payment.jwt.JwtTokenUtil;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.Commission;
import com.chh.trustfort.payment.model.Users;

import com.chh.trustfort.payment.payload.CommissionWithdrawalRequestPayload;
import com.chh.trustfort.payment.payload.CreditCommissionPayload;
import com.chh.trustfort.payment.payload.OmniResponsePayload;
import com.chh.trustfort.payment.repository.UsersRepository;
import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.CommissionService;
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
import java.util.List;

@Slf4j
@RestController
@RequestMapping(ApiPath.BASE_API + ApiPath.COMMISSION_BASE)
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CommissionController {

    private final CommissionService commissionService;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;

    @PostMapping(value = ApiPath.CREDIT_COMMISSION, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> creditCommission(
            @RequestHeader(name = "Authorization") String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        // 1. Validate request using token and payload
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                "CREDIT_COMMISSION", requestPayload, httpRequest, idToken
        );

        // 2. Handle validation errors
        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        // 3. Business logic
        CreditCommissionPayload data = gson.fromJson(request.payload, CreditCommissionPayload.class);
        String result = commissionService.creditCommission(
                request.appUser.getId(),
                data.getAmount(),
                CommissionType.valueOf(data.getSource()),
                data.getReference()
        );

        // 4. Return AES-encrypted response
        return new ResponseEntity<>(aesService.encrypt(result, request.appUser), HttpStatus.OK);
    }

    @GetMapping(value = ApiPath.USER_COMMISSIONS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserCommissions(
            @RequestHeader(name = "Authorization") String idToken,
            HttpServletRequest httpRequest
    ) {
        // 1. Validate request using your requestManager format
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                "GET_USER_COMMISSIONS", "", httpRequest, idToken
        );

        // 2. Handle validation failure
        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        // 3. Proceed with logic after validation
        List<Commission> commissions = commissionService.getUserCommissions(request.appUser.getId());

        // 4. Encrypt and return result
        return new ResponseEntity<>(aesService.encrypt(commissions.toString(), request.appUser), HttpStatus.OK);
    }


    @PostMapping(value = ApiPath.WITHDRAW_COMMISSION, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> withdrawCommission(
            @RequestHeader(name = "Authorization") String idToken,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                "WITHDRAW_COMMISSION", requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        try {
            WithdrawCommissionRequest withdrawRequest = gson.fromJson(request.payload, WithdrawCommissionRequest.class);
            String result = commissionService.withdrawCommission(withdrawRequest, request.appUser);
            return new ResponseEntity<>(aesService.encrypt(result, request.appUser), HttpStatus.OK);
        } catch (WalletException e) {
            return new ResponseEntity<>(
                    SecureResponseUtil.error(ResponseCode.FAILED_TRANSACTION.getResponseCode(), e.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }
    }


//    @PostMapping(ApiPath.WITHDRAW_COMMISSION)
//    public ResponseEntity<String> withdrawCommission(
//            @RequestBody WithdrawCommissionRequest payload,
//            @RequestAttribute("loggedInUser") Users users) {
//
//        String response = commissionService.withdrawCommission(payload, users);
//        return ResponseEntity.ok(response);
//    }





}


