package com.chh.trustfort.accounting.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "treasury_bill_investments")
public class TreasuryBillInvestment extends InvestmentVehicle {

    private BigDecimal adjustment;

    @Column(name = "yield_rate_pa", precision = 5, scale = 2)
    private BigDecimal yieldRatePA;

    @Column(name = "new_principal", precision = 5, scale = 2)
    private BigDecimal newPrincipal;

    @Column(name = "adj_interest", precision = 19, scale = 2)
    private BigDecimal adjInterest;

    @Column(name = "actual_interest", precision = 19, scale = 2)
    private BigDecimal actualInterest;

    @Column(name = "with_holding_tax", precision = 19, scale = 2)
    private BigDecimal withHoldingTax;

    @Column(name = "net_interest", precision = 19, scale = 2)
    private BigDecimal netInterest;

    @Column(name = "principal_plus_net_interest_ytd", precision = 19, scale = 2)
    private BigDecimal principalPlusNetInterestYTD;
}
