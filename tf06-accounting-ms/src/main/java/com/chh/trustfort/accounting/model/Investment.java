package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.InsuranceProductType;
import com.chh.trustfort.accounting.enums.InvestmentType;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "investment")
public class Investment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reference;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private InvestmentType type;

    @Enumerated(EnumType.STRING)
    private InsuranceProductType insuranceProductType;

    private boolean isParticipating;
    private boolean rolledOver;
    private boolean maturityNotified;

    @ManyToOne
    private AssetClass assetClass;
    @ManyToOne
    private Institution institution;

    private LocalDate startDate;
    private LocalDate maturityDate;
    private BigDecimal expectedReturn;

    private String createdBy;
    private LocalDateTime createdAt;
}

