package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.InsuranceProductType;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "investment_rule")
public class InvestmentRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private InsuranceProductType insuranceType;

    private Integer version;
    private boolean disabled = false;
    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investment_id")
    private Investment investment;

    private Integer minTenorYears;
    private BigDecimal maxAmount;
    private boolean allowHighRisk;
    private String createdBy;
    private LocalDateTime createdAt;
}
