package com.chh.trustfort.accounting.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_audit_trail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportAuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String action;

    private String reportType;

    private String requestParams;

    private LocalDateTime timestamp;
}
