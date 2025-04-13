package com.chh.trustfort.payment.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String walletId;
    private String userId;
    private String eventType; // e.g., "CREDIT", "WITHDRAWAL", "FAILED_TRANSFER"
    private String reference;
    private String description;

    private LocalDateTime createdAt = LocalDateTime.now();
}
