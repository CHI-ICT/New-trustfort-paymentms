package com.chh.trustfort.payment.controller;//package com.chh.trustfort.payment.controller;
//
//import com.chh.trustfort.payment.Quintuple;
//import com.chh.trustfort.payment.Util.SecureResponseUtil;
//import com.chh.trustfort.payment.component.RequestManager;
//import com.chh.trustfort.payment.component.Role;
//import com.chh.trustfort.payment.constant.ApiPath;
//import com.chh.trustfort.payment.model.AppUser;
//import com.chh.trustfort.payment.payload.OmniResponsePayload;
//import com.chh.trustfort.payment.service.NotificationService;
//import com.google.gson.Gson;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//
//
//@Slf4j
//@RestController
//@RequestMapping(ApiPath.BASE_API)
//@Tag(name = "Email Notification", description = "Test endpoint for email notifications")
//@RequiredArgsConstructor
//@SecurityRequirement(name = "bearerAuth")
//public class EmailTestController {
//
//    private final NotificationService notificationService;
//    private final Gson gson;
//    private final RequestManager requestManager;
//
//    @PostMapping(value = ApiPath.TEST_EMAIL_NOTIFICATION, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> sendTestEmail(@RequestHeader(ApiPath.ID_TOKEN) String idToken,
//                                           @RequestBody String requestPayload,
//                                           HttpServletRequest httpRequest) {
//
//        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
//                Role.TEST_EMAIL_NOTIFICATION.getValue(), requestPayload, httpRequest, idToken
//        );
//
//        if (request.isError) {
//            OmniResponsePayload errorResponse = gson.fromJson(request.payload, OmniResponsePayload.class);
//            return new ResponseEntity<>(
//                    SecureResponseUtil.error(errorResponse.getResponseCode(), errorResponse.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
//                    HttpStatus.OK
//            );
//        }
//
//        String to = "tobiajayi60@gmail.com";
//        String subject = "✅ Test Email from Trustfort";
//        String message = "This is a test email to verify notification setup.";
//
//        notificationService.sendEmail(to, subject, message);
//
//        String response = "Test email sent to " + to;
//        return new ResponseEntity<>(SecureResponseUtil.success(response), HttpStatus.OK);
//    }
//}