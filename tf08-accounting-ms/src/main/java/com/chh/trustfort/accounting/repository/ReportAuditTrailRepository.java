package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.ReportAuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportAuditTrailRepository extends JpaRepository<ReportAuditTrail, Long> {
}
