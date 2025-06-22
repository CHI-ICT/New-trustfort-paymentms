package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.RealTimeMonitoringDTO;

import java.time.LocalDate;

public interface RealTimeMonitoringService {
    RealTimeMonitoringDTO monitorTrends(LocalDate prevStart, LocalDate prevEnd, LocalDate currStart, LocalDate currEnd);
}
