package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.InsuranceProductType;
import com.chh.trustfort.accounting.enums.InvestmentType;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "money_market_investments")
public class MoneyMarketInvestment extends InvestmentVehicle {

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "nature_of_transaction")
    private String natureOfTransaction;

    @Column(name = "starting_principal", precision = 19, scale = 2)
    private BigDecimal startingPrincipal;

    @Column(name = "adjustment_to_opening_capital", precision = 19, scale = 2)
    private BigDecimal adjustmentToOpeningCapital;

    @Column(name = "capitalized_interest_to_date", precision = 19, scale = 2)
    private BigDecimal capitalizedInterestToDate;

    @Column(name = "principal_plus_capitalized_interest", precision = 19, scale = 2)
    private BigDecimal principalPlusCapitalizedInterest;

    @Column(name = "addition", precision = 19, scale = 2)
    private BigDecimal addition;

    @Column(name = "liquidation", precision = 19, scale = 2)
    private BigDecimal liquidation;

    @Column(name = "new_principal", precision = 19, scale = 2)
    private BigDecimal newPrincipal;

    @Column(name = "rate_pa", precision = 5, scale = 2)
    private BigDecimal ratePA;

    @Column(name = "value_date")
    private LocalDate valueDate;

    @Column(name = "end_of_period_date")
    private LocalDate endOfPeriodDate;

    @Column(name = "adj_interest", precision = 19, scale = 2)
    private BigDecimal adjustedInterest;

    @Column(name = "actual_interest", precision = 19, scale = 2)
    private BigDecimal actualInterest;

    @Column(name = "with_holding_tax", precision = 19, scale = 2)
    private BigDecimal withHoldingTax;

    @Column(name = "closing_principal", precision = 19, scale = 2)
    private BigDecimal closingPrincipal;

    @PrePersist
    public void prePersist() {
        this.setInvestmentType(InvestmentType.MONEY_MARKET);
    }
}
