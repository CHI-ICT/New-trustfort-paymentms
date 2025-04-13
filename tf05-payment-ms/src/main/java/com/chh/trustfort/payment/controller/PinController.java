package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Responses.ErrorResponse;
import com.chh.trustfort.payment.Responses.SuccessResponse;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.component.ResponseCode;
import com.chh.trustfort.payment.component.Role;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.SetupPinRequest;
import com.chh.trustfort.payment.dto.ValidatePinRequest;
import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.repository.UsersRepository;
import com.chh.trustfort.payment.service.PinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(ApiPath.BASE_API)
public class PinController {

    @Autowired
    private PinService pinService;

    @Autowired
    private RequestManager requestManager;

    @Autowired
    private UsersRepository usersRepository;
    @PostMapping(value = ApiPath.SETUP_PIN, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> setupTransactionPin(@RequestBody SetupPinRequest request, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, Users, String> result = requestManager.validateRequest(Role.SETUP_PIN.getValue(), "", httpRequest, ApiPath.ID_TOKEN);


        if (result.isError) return ResponseEntity.badRequest().body(result.payload);

        Users user = result.Users;
        user.setTransactionPin(pinService.hashPin(request.getRawPin()));
        usersRepository.save(user);

        SuccessResponse response = new SuccessResponse();
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("PIN set successfully");
        return ResponseEntity.ok(response);

    }

    @PostMapping(value = ApiPath.VALIDATE_PIN, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateTransactionPin(@RequestBody ValidatePinRequest request, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, Users, String> result = requestManager.validateRequest(
                "VALIDATE_PIN", request.getWalletId(), httpRequest, ApiPath.ID_TOKEN);

        if (result.isError) return ResponseEntity.badRequest().body(result.payload);

        Users user = result.Users;
        if (!pinService.matches(request.getRawPin(), user.getTransactionPin())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid PIN", ResponseCode.FAILED_TRANSACTION.getResponseCode()));
        }

        SuccessResponse response = new SuccessResponse();
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("PIN verified successfully");
        return ResponseEntity.ok(response);
        }
    }
