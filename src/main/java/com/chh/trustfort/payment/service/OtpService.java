package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.OtpToken;
import com.chh.trustfort.payment.repository.AppUserRepository;
import com.chh.trustfort.payment.repository.OtpTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private static final long EXPIRY_MINUTES = 3;

    private final NotificationService notificationService;
    private final AppUserRepository appUserRepository;
    private final OtpTokenRepository otpTokenRepository;

    public String generateOtp(Long userId) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        log.info("Generated OTP: {}", otp);

        OtpToken token = new OtpToken();
        token.setUserId(userId);
        token.setOtpCode(otp);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(EXPIRY_MINUTES));
        token.setUsed(false);
        otpTokenRepository.save(token);

        Optional<AppUser> userOpt = appUserRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.error("❌ User not found for ID: {}", userId);
            return otp;
        }

        AppUser user = userOpt.get();
        String message = "Your Trustfort OTP is: " + otp + ". Valid for " + EXPIRY_MINUTES + " minutes.";

        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank()) {
            log.info("📲 Sending OTP SMS to: {}", user.getPhoneNumber());
            notificationService.sendSms(user.getPhoneNumber(), message);
        } else {
            log.warn("⚠️ User has no phone number.");
        }

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            log.info("📧 Sending OTP Email to: {}", user.getEmail());
            notificationService.sendEmail(user.getEmail(), "Your OTP Code", message);
        } else {
            log.warn("⚠️ User has no email address.");
        }

        return otp;
    }

    public boolean validateOtp(Long userId, String otp, String withdrawFunds) {
        return otpTokenRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .filter(token -> !token.isUsed()
                        && token.getOtpCode().equals(otp)
                        && token.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(token -> {
                    token.setUsed(true);
                    otpTokenRepository.save(token);
                    return true;
                }).orElse(false);
    }
}