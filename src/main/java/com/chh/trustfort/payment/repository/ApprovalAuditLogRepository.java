package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.model.facility.ApprovalAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalAuditLogRepository extends JpaRepository<ApprovalAuditLog, Long> {
}

