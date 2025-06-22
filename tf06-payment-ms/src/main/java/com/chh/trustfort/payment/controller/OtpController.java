package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Responses.SuccessResponse;
import com.chh.trustfort.payment.Util.SecureResponseUtil;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.component.Role;
import com.chh.trustfort.payment.dto.SetupPinRequest;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.payload.OmniResponsePayload;
import com.chh.trustfort.payment.payload.OtpRequestPayload;
import com.chh.trustfort.payment.repository.UsersRepository;
import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.OtpService;
import com.chh.trustfort.payment.service.PinService;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(ApiPath.BASE_API)
public class OtpController {

    private final OtpService otpService;
    private final RequestManager requestManager;
    private final Gson gson;
    private final AesService aesService;

    @PostMapping(value = ApiPath.GENERATE_OTP, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateOtp(@RequestParam String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.GENERATE_OTP.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        OtpRequestPayload payload = gson.fromJson(request.payload, OtpRequestPayload.class);
        if (payload.getUserId() == null) {
            return new ResponseEntity<>(
                    SecureResponseUtil.error("400", "User ID is required", "BAD_REQUEST"),
                    HttpStatus.OK
            );
        }

        String otp = otpService.generateOtp(payload.getUserId());
        return new ResponseEntity<>(aesService.encrypt("OTP has been generated and sent via SMS and Email", request.appUser), HttpStatus.OK);
    }
}