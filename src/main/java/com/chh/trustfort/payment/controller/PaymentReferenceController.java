//package com.chh.trustfort.payment.controller;
//
//import com.chh.trustfort.payment.Quintuple;
//import com.chh.trustfort.payment.Responses.ApiResponse;
//import com.chh.trustfort.payment.component.RequestManager;
//import com.chh.trustfort.payment.component.Role;
//import com.chh.trustfort.payment.constant.ApiPath;
//import com.chh.trustfort.payment.model.PaymentReference;
//import com.chh.trustfort.payment.model.Users;
//import com.chh.trustfort.payment.payload.AmountPayload;
//import com.chh.trustfort.payment.payload.PaymentReferenceRequestPayload;
//import com.chh.trustfort.payment.security.AesService;
//import com.chh.trustfort.payment.service.PaymentReferenceService;
//import com.google.gson.Gson;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import java.math.BigDecimal;
//
//@RestController
//@RequestMapping(ApiPath.BASE_API) // or "/trustfort/api/v1"
//public class PaymentReferenceController {
//
//    @Autowired
//    private Gson gson;
//
//    @Autowired
//    private AesService aesService;
//
//    @Autowired
//    private PaymentReferenceService referenceService;
//
//    @Autowired
//    private RequestManager requestManager; // used for auth and validation
//
//    /**
//     * Generate a virtual payment reference code
//     */
////    @PostMapping(value = ApiPath.GENERATE_PAYMENT_REFERENCE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
////    public ResponseEntity<?> generateReference(@RequestBody PaymentReferenceRequestPayload requestPayload, HttpServletRequest httpRequest) throws Exception {
////
////        Quintuple<Boolean, String, String, Users, String> request = requestManager.validateRequest(
////                com.chh.trustfort.payment.component.Role.GENERATE_PAYMENT_REFERENCE.getValue(),
////                gson.toJson(requestPayload), httpRequest, ApiPath.ID_TOKEN
////        );
////        if (request.isError) {
////            return new ResponseEntity<>(request.payload, HttpStatus.OK);
////        }
////
////        PaymentReferenceRequestPayload payload = gson.fromJson(request.payload, PaymentReferenceRequestPayload.class);
////        String response = referenceService.generatePaymentReference(payload, request.Users);
////        return ResponseEntity.ok(response);
////    }
//    @PostMapping(value = ApiPath.GENERATE_PAYMENT_REFERENCE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> generateReference(@RequestBody PaymentReferenceRequestPayload requestPayload, HttpServletRequest httpRequest) throws Exception {
//
//        Quintuple<Boolean, String, String, Users, String> request = requestManager.validateRequest(
//                Role.GENERATE_PAYMENT_REFERENCE.getValue(),
//                gson.toJson(requestPayload),
//                httpRequest,
//                ApiPath.ID_TOKEN
//        );
//
//        if (request.isError) {
//            return ResponseEntity.ok(request.payload);
//        }
//
//        PaymentReferenceRequestPayload payload = gson.fromJson(request.payload, PaymentReferenceRequestPayload.class);
//        String response = referenceService.generatePaymentReference(payload, request.Users);
//        return ResponseEntity.ok(response);
//    }
//}
