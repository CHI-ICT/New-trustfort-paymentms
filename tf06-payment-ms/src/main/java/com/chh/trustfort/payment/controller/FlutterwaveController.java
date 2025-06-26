//package com.chh.trustfort.payment.controller;
//
//import com.chh.trustfort.payment.Quintuple;
//import com.chh.trustfort.payment.Responses.VerifyFlutterwaveResponse;
//import com.chh.trustfort.payment.Util.SecureResponseUtil;
//import com.chh.trustfort.payment.component.RequestManager;
//import com.chh.trustfort.payment.component.Role;
//import com.chh.trustfort.payment.constant.ApiPath;
//import com.chh.trustfort.payment.dto.InitiatePaymentRequest;
//import com.chh.trustfort.payment.dto.VerifyFlutterwaveRequest;
//import com.chh.trustfort.payment.model.AppUser;
//import com.chh.trustfort.payment.payload.OmniResponsePayload;
//import com.chh.trustfort.payment.security.AesService;
//import com.chh.trustfort.payment.service.FlutterwaveService;
//import com.google.gson.Gson;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//@SecurityRequirement(name = "bearerAuth")
//@RequestMapping(ApiPath.BASE_API)
//public class FlutterwaveController {
//
//    private final FlutterwaveService flutterwaveService;
//    private final RequestManager requestManager;
//    private final Gson gson;
//    private final AesService aesService;
//
//    @PostMapping(value = ApiPath.VERIFY_FLW_TRANSACTION, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> verifyTransaction(
//            @RequestParam String idToken,
//            @RequestBody String encryptedPayload,
//            HttpServletRequest httpRequest
//    ) {
//        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
//                Role.VERIFY_FLW_TRANSACTION.getValue(), encryptedPayload, httpRequest, idToken
//        );
//
//        if (request.isError) {
//            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
//            return new ResponseEntity<>(
//                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
//                    HttpStatus.OK
//            );
//        }
//
//        VerifyFlutterwaveRequest payload = gson.fromJson(request.payload, VerifyFlutterwaveRequest.class);
//        VerifyFlutterwaveResponse result = flutterwaveService.verifyTransaction(payload);
//        return new ResponseEntity<>(aesService.encrypt(String.valueOf(result), request.appUser), HttpStatus.OK);
//    }
//
//    @PostMapping(value = ApiPath.INITIATE_FLW_PAYMENT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> initiatePayment(
//            @RequestParam String idToken,
//            @RequestBody String encryptedPayload,
//            HttpServletRequest httpRequest
//    ) {
//        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
//                Role.INITIATE_FLW_PAYMENT.getValue(), encryptedPayload, httpRequest, idToken
//        );
//
//        if (request.isError) {
//            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
//            return new ResponseEntity<>(
//                    SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
//                    HttpStatus.OK
//            );
//        }
//
//        InitiatePaymentRequest payload = gson.fromJson(request.payload, InitiatePaymentRequest.class);
//        String result = flutterwaveService.initiatePayment(payload);
//        return new ResponseEntity<>(aesService.encrypt(result, request.appUser), HttpStatus.OK);
//    }
//}