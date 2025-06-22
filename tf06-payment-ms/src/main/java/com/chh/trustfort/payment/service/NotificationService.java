package com.chh.trustfort.payment.service;

public interface NotificationService {
    void sendEmail(String to, String subject, String message);
    void sendSms(String phoneNumber, String message);
}
