package com.chh.trustfort.payment.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PinService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String hashPin(String pin) {
        return passwordEncoder.encode(pin);
    }

    public boolean matches(String rawPin, String hashedPin) {
        return passwordEncoder.matches(rawPin, hashedPin);
    }
}
