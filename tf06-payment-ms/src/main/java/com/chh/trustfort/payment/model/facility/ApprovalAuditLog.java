package com.chh.trustfort.payment.model.facility;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "approval_audit_log")
public class ApprovalAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long creditLineId;
    private Long approvalId;
    private Long approverId;
    private String action; // e.g. APPROVED, REJECTED, CREATED
    private String comment;

    private LocalDateTime actionTime;
}
