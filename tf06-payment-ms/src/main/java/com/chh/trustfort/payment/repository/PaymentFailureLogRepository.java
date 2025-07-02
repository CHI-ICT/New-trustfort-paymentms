package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.model.PaymentFailureLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentFailureLogRepository extends JpaRepository<PaymentFailureLog, Long> {
}
