package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.dto.EmailDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendEmail(EmailDetails emailDetails) {
        Logger logger = LoggerFactory.getLogger(EmailDetails.class);
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(from);
            mailMessage.setTo(emailDetails.getRecipient());
            mailMessage.setSubject(emailDetails.getSubject());
            mailMessage.setText(emailDetails.getBody());
            javaMailSender.send(mailMessage);
        } catch (MailException e) {
            logger.error("Failed to send email: {}", e.getMessage());
            throw new IllegalArgumentException(e);
        }
        System.out.println("Inside EmailService - Sending email to: " + emailDetails.getRecipient());

    }

    @PostConstruct
    public void testDecryption() {
        System.out.println("📧 Mailgun Username (Decrypted): " + from);
    }

}
