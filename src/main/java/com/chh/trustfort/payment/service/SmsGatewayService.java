package com.chh.trustfort.payment.service;

import org.springframework.stereotype.Service;

@Service
public class SmsGatewayService {

    public void sendSms(String phoneNumber, String message) {
        // Replace with Termii/Twilio logic
        System.out.println("📱 Sending SMS to " + phoneNumber + ": " + message);
    }
}
