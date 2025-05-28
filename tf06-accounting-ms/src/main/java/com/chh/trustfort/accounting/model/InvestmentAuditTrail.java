package com.chh.trustfort.accounting.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "investment_audit_trail")
public class InvestmentAuditTrail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long investmentId;
    private String action;
    private String performedBy;
    private LocalDateTime performedAt;
    private String details;
}
