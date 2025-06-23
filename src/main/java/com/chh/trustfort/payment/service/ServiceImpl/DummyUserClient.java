package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.service.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class DummyUserClient implements UserClient {

    private final Map<Long, String> userPins = new ConcurrentHashMap<>();

    @Override
    public void updateTransactionPin(Long userId, String hashedPin) {
        userPins.put(userId, hashedPin);
        log.info("🔐 Dummy: PIN updated for user ID {}", userId);
    }

    @Override
    public String getHashedTransactionPin(Long userId) {
        String pin = userPins.get(userId);
        if (pin == null) {
            throw new RuntimeException("Dummy: PIN not set for user ID " + userId);
        }
        return pin;
    }
}
