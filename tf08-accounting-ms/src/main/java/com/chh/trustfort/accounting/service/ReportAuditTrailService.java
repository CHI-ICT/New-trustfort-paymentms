package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.ReportAuditTrail;
import com.chh.trustfort.accounting.repository.ReportAuditTrailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportAuditTrailService {

    private final ReportAuditTrailRepository auditRepo;

    public void log(String username, String action, String reportType, String requestParams) {
        ReportAuditTrail log = ReportAuditTrail.builder()
                .username(username)
                .action(action)
                .reportType(reportType)
                .requestParams(requestParams)
                .timestamp(LocalDateTime.now())
                .build();

        auditRepo.save(log);
    }
}
