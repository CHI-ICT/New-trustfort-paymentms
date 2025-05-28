package com.chh.trustfort.payment.model.facility;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Data
@Table(name = "approval_rule")
public class ApprovalRule {

    @Id
    @GeneratedValue
    private Long id;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Boolean isDeleted = false;
    private Integer level;
    private Long approverId;
    @Column(name = "created_by", nullable = true, length = 80)
    private String createdBy;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_by", nullable = true, length = 20)
    private String updatedBy;
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt = null;
}
