package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {
}
