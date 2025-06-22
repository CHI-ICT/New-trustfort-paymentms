package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.ApprovalStatus;
import com.chh.trustfort.accounting.enums.TaxType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tax_approvals")
public class TaxApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private TaxType taxType;

    private BigDecimal amount;

    private LocalDate filingPeriodStart;

    private LocalDate filingPeriodEnd;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    private String comments; // (Optional) Comments at each stage
}
