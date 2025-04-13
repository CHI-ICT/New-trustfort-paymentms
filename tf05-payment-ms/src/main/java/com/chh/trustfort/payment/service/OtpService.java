package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.model.OtpToken;
import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.repository.OtpTokenRepository;
import com.chh.trustfort.payment.repository.UsersRepository;
import com.chh.trustfort.payment.service.ServiceImpl.WalletServiceImpl;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Data
@Service
public class OtpService {
    private static final Logger log = LoggerFactory.getLogger(OtpService.class);

    private static final long EXPIRY_MINUTES = 3; // ⏱️ OTP validity duration

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UsersRepository usersRepository; // To fetch phone/email


    @Autowired
    private OtpTokenRepository otpTokenRepository;

    public String generateOtp(Long userId) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        log.info("Generated OTP: {}", otp);

        OtpToken token = new OtpToken();
        token.setUserId(userId);
        token.setOtpCode(otp);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(3));
        token.setUsed(false);
        otpTokenRepository.save(token);
        log.info("OTP token saved to database for userId: {}", userId);

        // Fetch user to send notification
        Optional<Users> userOpt = usersRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.error("❌ User not found for ID: {}", userId);
            return otp;
        }

        Users user = userOpt.get();
        String message = "Your Trustfort OTP is: " + otp + ". Valid for " + EXPIRY_MINUTES + " minutes.";

        // Send via SMS
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank()) {
            log.info("📲 Sending OTP SMS to: {}", user.getPhoneNumber());
            notificationService.sendSms(user.getPhoneNumber(), message);
        } else {
            log.warn("⚠️ User has no phone number.");
        }

        // Send via Email
        if (user.getEmailAddress() != null && !user.getEmailAddress().isBlank()) {
            log.info("📧 Sending OTP Email to: {}", user.getEmailAddress());
            notificationService.sendEmail(user.getEmailAddress(), "Your OTP Code", message);
        } else {
            log.warn("⚠️ User has no email address.");
        }

        return otp;
    }

    public boolean validateOtp(Long userId, String otp, String withdrawFunds) {
        return otpTokenRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .filter(token -> !token.isUsed() &&
                        token.getOtpCode().equals(otp) &&
                        token.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(token -> {
                    token.setUsed(true);
                    otpTokenRepository.save(token);
                    return true;
                })
                .orElse(false);
    }
}