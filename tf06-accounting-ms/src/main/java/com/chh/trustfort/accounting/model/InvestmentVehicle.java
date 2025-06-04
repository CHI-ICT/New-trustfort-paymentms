package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.InsuranceProductType;
import com.chh.trustfort.accounting.enums.InvestmentSubtype;
import com.chh.trustfort.accounting.enums.InvestmentType;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "investment_vehicles")
@Inheritance(strategy = InheritanceType.JOINED)
//@DiscriminatorColumn(name = "investment_type")
public abstract class InvestmentVehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "reference")
    private String reference;

    @Column(name = "amount")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "insurance_product_type")
    private InsuranceProductType insuranceProductType;

    @Column(name = "is_participating")
    private boolean isParticipating;

    @Column(name = "rolled_over")
    private boolean rolledOver;

    @Column(name = "maturity_notified")
    private boolean maturityNotified;

    @Column(name = "name")
    private String name;

    @Column(name = "currency")
    private String currency;

    @Column(name = "principal", precision = 19, scale = 2)
    private BigDecimal principal;

    @Column(name = "net_interest", precision = 19, scale = 2)
    private BigDecimal netInterest;

    @Column(name = "tenor")
    private Long tenor;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "maturity_date")
    private LocalDate maturityDate;

    @Column(name = "expected_return", precision = 19, scale = 2)
    private BigDecimal expectedReturn;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "roi", precision = 19, scale = 2)
    private BigDecimal roi;

    @Column(name = "interest", precision = 19, scale = 2)
    private BigDecimal interest;

    @Column(name = "dividends", precision = 19, scale = 2)
    private BigDecimal dividends;

    @Enumerated(EnumType.STRING)
    @Column(name = "investment_type")
    private InvestmentType investmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "subtype")
    private InvestmentSubtype subtype;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;
}

