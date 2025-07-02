package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.model.WebhookLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WebhookLogRepository extends JpaRepository<WebhookLog, Long> {
    Optional<WebhookLog> findByReference(String reference);
    boolean existsByTxRef(String txRef);

}
