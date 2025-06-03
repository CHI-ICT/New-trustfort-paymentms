package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.InvestmentType;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "commercial_paper_investments")
public class CommercialPaperInvestment extends InvestmentVehicle {

    @Column(name = "investment_date")
    private LocalDate date;

    @Column(name = "nature_of_investment")
    private String natureOfInvestment;

    @Column(name = "addition_liquidation", precision = 19, scale = 2)
    private BigDecimal additionOrLiquidation;

    @Column(name = "new_principal", precision = 19, scale = 2)
    private BigDecimal newPrincipal;

    @Column(name = "yield_rate_pa", precision = 5, scale = 2)
    private BigDecimal yieldRatePA;

    @Column(name = "commencement")
    private LocalDate commencementDate;

    @Column(name = "maturity")
    private LocalDate maturityDate;

    @Column(name = "tenor")
    private Long tenor;

    @Column(name = "interest", precision = 19, scale = 2)
    private BigDecimal interest;

    @Column(name = "adjustment", precision = 19, scale = 2)
    private BigDecimal adjustment;

    @Column(name = "actual_interest", precision = 19, scale = 2)
    private BigDecimal actualInterest;

    @Column(name = "with_holding_tax", precision = 19, scale = 2)
    private BigDecimal wht;

    @Column(name = "net_interest", precision = 19, scale = 2)
    private BigDecimal netInterest;

    @PrePersist
    public void prePersist() {
        this.setInvestmentType(InvestmentType.COMMERCIAL_PAPER);
    }
}
