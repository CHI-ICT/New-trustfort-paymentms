package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Responses.ErrorResponse;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.component.ResponseCode;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.CommissionRequest;
import com.chh.trustfort.payment.dto.WithdrawCommissionRequest;
import com.chh.trustfort.payment.enums.CommissionType;
import com.chh.trustfort.payment.exception.WalletException;
import com.chh.trustfort.payment.jwt.JwtTokenUtil;
import com.chh.trustfort.payment.model.Commission;
import com.chh.trustfort.payment.model.Users;

import com.chh.trustfort.payment.payload.CommissionWithdrawalRequestPayload;
import com.chh.trustfort.payment.payload.CreditCommissionPayload;
import com.chh.trustfort.payment.repository.UsersRepository;
import com.chh.trustfort.payment.service.CommissionService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(ApiPath.BASE_API + ApiPath.COMMISSION_BASE)
@RequiredArgsConstructor
public class CommissionController {

    private final CommissionService commissionService;
    private final Gson gson;
    @Autowired
    private RequestManager requestManager;

    @Autowired
    private JwtTokenUtil jwtValidator;

    @Autowired
    private UsersRepository usersRepository;


    @PostMapping(value = ApiPath.CREDIT_COMMISSION, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> creditCommission(@RequestBody CreditCommissionPayload payload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, Users, String> request = requestManager.validateRequest("CREDIT_COMMISSION", gson.toJson(payload)
                , httpRequest, ApiPath.ID_TOKEN);
        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        CreditCommissionPayload data = gson.fromJson(request.payload, CreditCommissionPayload.class);
        String response = commissionService.creditCommission(request.Users.getId(), data.getAmount(), CommissionType.valueOf(data.getSource()), data.getReference());
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = ApiPath.USER_COMMISSIONS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCommissions(@RequestHeader(ApiPath.ID_TOKEN) String token, String encryptionKey) throws Exception {
        String username = jwtValidator.getUserNameFromToken(token, encryptionKey);
        Users user = usersRepository.getUserByUserName(username); // Now you have the full Users object

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user token");
        }

        List<Commission> commissions = commissionService.getUserCommissions(user.getId());
        return ResponseEntity.ok(commissions);
    }

    @PostMapping(value = ApiPath.WITHDRAW_COMMISSION, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> withdrawCommission(@RequestBody WithdrawCommissionRequest request, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, Users, String> result = requestManager.validateRequest("WITHDRAW_COMMISSION", request.getWalletId(), httpRequest, ApiPath.ID_TOKEN);

        if (result.isError) return ResponseEntity.badRequest().body(result.payload);

        try {
            String response = commissionService.withdrawCommission(request, result.Users);
            return ResponseEntity.ok(response);
        } catch (WalletException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage(), ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }
    }

    @PostMapping(ApiPath.WITHDRAW_COMMISSION)
    public ResponseEntity<String> withdrawCommission(
            @RequestBody WithdrawCommissionRequest payload,
            @RequestAttribute("loggedInUser") Users users) {

        String response = commissionService.withdrawCommission(payload, users);
        return ResponseEntity.ok(response);
    }





}


