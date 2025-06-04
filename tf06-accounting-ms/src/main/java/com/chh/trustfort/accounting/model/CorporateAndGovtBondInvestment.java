package com.chh.trustfort.accounting.model;
import com.chh.trustfort.accounting.enums.InvestmentSubtype;
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
@Table(name = "corporate_govt_bond_investments")
public class CorporateAndGovtBondInvestment extends InvestmentVehicle {

    @Column(name = "bond_series")
    private String bondSeries;

    @Column(name = "volume", precision = 19, scale = 2)
    private BigDecimal volume;

    @Column(name = "face_value", precision = 19, scale = 2)
    private BigDecimal faceValue;

    @Column(name = "cost_per_unit", precision = 19, scale = 2)
    private BigDecimal costPerUnit;

    @Column(name = "settlement_value", precision = 19, scale = 2)
    private BigDecimal settlementValue;

    @Column(name = "capital_repayment", precision = 19, scale = 2)
    private BigDecimal capitalRepayment;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "last_coupon_date")
    private LocalDate lastCouponDate;

    @Column(name = "coupon_rate", precision = 5, scale = 2)
    private BigDecimal couponRate;

    @Column(name = "daily_accrual", precision = 19, scale = 2)
    private BigDecimal dailyAccrual;

    @Column(name = "interest_receivable", precision = 19, scale = 2)
    private BigDecimal interestReceivable;

    @Column(name = "discount_or_premium", precision = 19, scale = 2)
    private BigDecimal discountOrPremium;

    @Column(name = "daily_amortization", precision = 19, scale = 2)
    private BigDecimal dailyAmortization;

    @Column(name = "expired_amortization", precision = 19, scale = 2)
    private BigDecimal expiredAmortization;

    @Column(name = "outstanding_amortization", precision = 19, scale = 2)
    private BigDecimal outstandingAmortization;

    @Column(name = "carrying_value", precision = 19, scale = 2)
    private BigDecimal carryingValue;

    @Column(name = "carrying_value_with_interest", precision = 19, scale = 2)
    private BigDecimal carryingValueWithInterest;

    @Column(name = "effective_interest_rate", precision = 5, scale = 2)
    private BigDecimal effectiveInterestRate;

    @Column(name = "coupon_payments", precision = 19, scale = 2)
    private BigDecimal couponPayments;

    @Column(name = "interest_cost_eir", precision = 19, scale = 2)
    private BigDecimal interestCostEir;

    @Column(name = "amortised_cost", precision = 19, scale = 2)
    private BigDecimal amortisedCost;

    @Column(name = "market_price", precision = 19, scale = 2)
    private BigDecimal marketPrice;

    @Column(name = "market_value", precision = 19, scale = 2)
    private BigDecimal marketValue;

    @Column(name = "unrealized_gain_loss", precision = 19, scale = 2)
    private BigDecimal unrealizedGainLoss;

    @Column(name = "interest_receivable_naira", precision = 19, scale = 2)
    private BigDecimal interestReceivableNaira;

    public int getDaysInPeriod() {
        if (this.getStartDate() != null && this.getEndDate() != null) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(this.getStartDate(), this.getEndDate());
        }
        return 0;
    }

    @PrePersist
    public void prePersist() {
        this.setInvestmentType(InvestmentType.FIXED_INCOME);
        this.setSubtype(InvestmentSubtype.CORPORATE_AND_GOVT_BOND);
    }
}
