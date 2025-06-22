package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.ReportApproval;
import com.chh.trustfort.accounting.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportApprovalRepository extends JpaRepository<ReportApproval, Long> {
    List<ReportApproval> findByReportId(Long reportId);
    List<ReportApproval> findByApprovalStatus(ApprovalStatus status);

}
