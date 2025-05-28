package com.chh.trustfort.accounting.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Data
@Table(name = "trial_balance_audit_trail")
public class TrialBalanceAuditTrail {
    @Id
    @GeneratedValue
    private Long id;
    private String accountCode;
    private BigDecimal oldAmount;
    private BigDecimal newAmount;
    private String reason;
    private String adjustedBy;
    private LocalDateTime timestamp;
}
