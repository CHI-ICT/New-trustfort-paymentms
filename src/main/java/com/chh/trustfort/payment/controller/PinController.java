package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Responses.ErrorResponse;
import com.chh.trustfort.payment.Responses.SuccessResponse;
import com.chh.trustfort.payment.Util.SecureResponseUtil;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.component.ResponseCode;
import com.chh.trustfort.payment.component.Role;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.SetupPinRequest;
import com.chh.trustfort.payment.dto.ValidatePinRequest;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.payload.OmniResponsePayload;
import com.chh.trustfort.payment.repository.UsersRepository;
import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.PinService;
import com.chh.trustfort.payment.service.UserClient;
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

@RestController
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(ApiPath.BASE_API)
public class PinController {

    private final PinService pinService;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;


    // Replace direct repo access with a User module boundary
    private final UserClient userClient; // <-- This is a hypothetical interface

    @PostMapping(value = ApiPath.SETUP_PIN, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> setupTransactionPin(@RequestParam String idToken,
                                                 @RequestBody String requestPayload,
                                                 HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.SETUP_PIN.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        SetupPinRequest payload = gson.fromJson(request.payload, SetupPinRequest.class);
        String hashedPin = pinService.hashPin(payload.getRawPin());

        // ✅ Call User module to save the pin
        userClient.updateTransactionPin(request.appUser.getId(), hashedPin);

        String encryptedResponse = aesService.encrypt("PIN set successfully", request.appUser);
        return new ResponseEntity<>(encryptedResponse, HttpStatus.OK);
    }

    @PostMapping(value = ApiPath.VALIDATE_PIN, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateTransactionPin(@RequestParam String idToken,
                                                    @RequestBody String requestPayload,
                                                    HttpServletRequest httpRequest) {

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.VALIDATE_PIN.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        ValidatePinRequest payload = gson.fromJson(request.payload, ValidatePinRequest.class);

        // ✅ Call User module to get hashed PIN
        String hashedPin = userClient.getHashedTransactionPin(request.appUser.getId());

        if (!pinService.matches(payload.getRawPin(), hashedPin)) {
            return new ResponseEntity<>(
                    aesService.encrypt("Invalid PIN", request.appUser),
                    HttpStatus.UNAUTHORIZED
            );
        }

        String encryptedResponse = aesService.encrypt("PIN verified successfully", request.appUser);
        return new ResponseEntity<>(encryptedResponse, HttpStatus.OK);
    }
}