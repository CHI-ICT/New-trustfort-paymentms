package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.Util.PhoneNumberUtil;
import com.chh.trustfort.payment.dto.EmailDetails;
import com.chh.trustfort.payment.service.EmailService;
import com.chh.trustfort.payment.service.NotificationService;
import com.chh.trustfort.payment.service.SmsGatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmailService emailService;
    private final SmsGatewayService smsGatewayService;

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
            log.info("📧 Sending email to {} with subject: {}", to, subject);
            emailService.sendEmail(email);
            log.info("✅ Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("❌ Failed to send email to {}: {}", to, e.getMessage(), e);
        }
    }

    @Override
    public void sendSms(String phoneNumber, String message) {
        if (!StringUtils.hasText(phoneNumber) || !StringUtils.hasText(message)) {
            log.warn("Invalid SMS input: empty phone or message");
            return;
        }

        if (!PhoneNumberUtil.isValidPhoneNumber(phoneNumber)) {
            log.warn("Invalid phone number format: {}", phoneNumber);
            return;
        }

        try {
            smsGatewayService.sendSms(phoneNumber, message);
            log.info("📲 SMS sent to {}", phoneNumber);
        } catch (Exception e) {
            log.error("❌ Failed to send SMS to {}: {}", phoneNumber, e.getMessage(), e);
        }
    }
}