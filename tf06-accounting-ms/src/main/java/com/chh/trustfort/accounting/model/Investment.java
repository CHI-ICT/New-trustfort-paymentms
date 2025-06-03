package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.InsuranceProductType;
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
@Table(name = "investments")
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference", unique = true, nullable = false)
    private String reference;

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "maturity_date", nullable = false)
    private LocalDate maturityDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "is_participating")
    private boolean isParticipating;

    @Column(name = "roi", precision = 5, scale = 2)
    private BigDecimal roi;

    @Column(name = "maturity_notified")
    private boolean maturityNotified;

    @Column(name = "rolled_over")
    private boolean rolledOver;

    @Enumerated(EnumType.STRING)
    @Column(name = "insurance_product_type")
    private InsuranceProductType insuranceProductType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "asset_id", referencedColumnName = "id")
    private InvestmentVehicle asset;

    @ManyToOne(optional = false)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;
}

