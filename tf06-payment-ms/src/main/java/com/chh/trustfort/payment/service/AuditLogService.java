package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.model.AuditLog;
import com.chh.trustfort.payment.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void logEvent(String walletId, String userId, String eventType, String reference, String description) {
        AuditLog log = new AuditLog();
        log.setWalletId(walletId);
        log.setUserId(userId);
        log.setEventType(eventType);
        log.setReference(reference);
        log.setDescription(description);
        auditLogRepository.save(log);
    }
}
