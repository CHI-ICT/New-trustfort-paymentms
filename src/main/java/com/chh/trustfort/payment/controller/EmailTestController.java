package com.chh.trustfort.payment.controller;

import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




@RestController
@RequestMapping(ApiPath.BASE_API)
@Tag(name = " Email Notification", description = "Email notification testing")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class EmailTestController {

    private final NotificationService notificationService;


    @PostMapping(ApiPath.TEST_EMAIL_NOTIFICATION)
    public String sendTestEmail() {
        String to = "tobiajayi60@gmail.com";  // 👈 your own email
        String subject = "✅ Test Email from Trustfort";
        String body = "This is a test email to verify notification setup.";

        notificationService.sendEmail(to, subject, body);
        return "Email sent to " + to;
    }
}
