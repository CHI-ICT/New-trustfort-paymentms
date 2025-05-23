package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.CreditStatus;
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
@Table(name = "credit-line")
public class CreditLine {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;

    private BigDecimal amount;

    private BigDecimal repaidAmount;

    @Enumerated(EnumType.STRING)
    private CreditStatus status;

    private LocalDateTime requestedAt;

    private LocalDateTime approvedDate;

    // Optional: for audit
    private String reason;
}
