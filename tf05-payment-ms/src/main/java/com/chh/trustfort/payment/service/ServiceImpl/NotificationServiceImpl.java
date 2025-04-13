package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.Util.PhoneNumberUtil;
import com.chh.trustfort.payment.dto.EmailDetails;
import com.chh.trustfort.payment.service.EmailService;
import com.chh.trustfort.payment.service.NotificationService;
import com.chh.trustfort.payment.service.SmsGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsGatewayService smsGatewayService;

    @Override
    public void sendEmail(String to, String subject, String message) {
        if (!StringUtils.hasText(to)) {
            log.warn("Empty recipient email");
            return;
        }

        EmailDetails email = new EmailDetails();
        email.setRecipient(to);
        email.setSubject(subject);
        email.setBody(message);

        try {
            emailService.sendEmail(email);
            log.info("Preparing to send email to {} with subject {}", to, subject);
            log.info("Email successfully sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    @Override
    public void sendSms(String phoneNumber, String message) {
        if (!StringUtils.hasText(phoneNumber) || !StringUtils.hasText(message)) {
            log.warn("Invalid SMS input");
            return;
        }

        if (!PhoneNumberUtil.isValidPhoneNumber(phoneNumber)) {
            log.warn("Invalid phone number format: {}", phoneNumber);
            return;
        }

        try {
            smsGatewayService.sendSms(phoneNumber, message);
            log.info("SMS sent to {}", phoneNumber);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage());
        }
    }
}
