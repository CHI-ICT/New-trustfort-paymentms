package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Responses.SuccessResponse;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.component.Role;
import com.chh.trustfort.payment.dto.SetupPinRequest;
import com.chh.trustfort.payment.payload.OtpRequestPayload;
import com.chh.trustfort.payment.repository.UsersRepository;
import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.service.OtpService;
import com.chh.trustfort.payment.service.PinService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(ApiPath.BASE_API)
public class OtpController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private Gson gson;

    @Autowired
    private RequestManager requestManager;


    @PostMapping(value = ApiPath.GENERATE_OTP, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateOtp(@RequestBody OtpRequestPayload requestPayload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, Users, String> request =
                requestManager.validateRequest(Role.GENERATE_OTP.getValue(), gson.toJson(requestPayload)
                        , httpRequest, ApiPath.ID_TOKEN);

        if (request.isError) {
            return new ResponseEntity<>(request.payload, HttpStatus.OK);
        }

        OtpRequestPayload payload = gson.fromJson(request.payload, OtpRequestPayload.class);

        if (payload.getUserId() == null) {
            return ResponseEntity.badRequest().body("User ID is required to generate OTP");
        }

        String otp = otpService.generateOtp(payload.getUserId());
        return ResponseEntity.ok("OTP has been generated and sent via SMS and Email");

    }

}
