package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.FxRateLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FxRateLogRepository extends JpaRepository<FxRateLog, Long> {
}