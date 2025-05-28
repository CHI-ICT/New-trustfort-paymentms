package com.chh.trustfort.payment.model.facility;

import com.chh.trustfort.payment.enums.CreditApprovalStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Data
@Table(name = "credit_approval")
public class CreditApproval {

    @Id
    @GeneratedValue
    private Long id;

    private Long creditLineId;

    private Integer level;

    private Long approverId;

    @Enumerated(EnumType.STRING)
    private CreditApprovalStatus status = CreditApprovalStatus.PENDING;

    private String comment;

    private LocalDateTime decisionDate;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

