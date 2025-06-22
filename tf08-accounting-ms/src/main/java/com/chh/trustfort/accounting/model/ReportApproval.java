package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.ApprovalStatus;
import com.chh.trustfort.accounting.enums.ApprovalLevel;
import com.chh.trustfort.accounting.enums.ReportType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_approval")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reportId;

    @Column(nullable = false)
    private String approverEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus;

    @Column(length = 500)
    private String remarks;

    @Column(nullable = false)
    private LocalDateTime approvedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalLevel approvalLevel;

    private String nextApprover;

    @Column(nullable = false)
    private String reportStatus; // PENDING, COMPLETED

//    @Column(nullable = false)
//    private String reportType;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType;

}