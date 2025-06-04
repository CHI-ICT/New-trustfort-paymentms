package com.chh.trustfort.accounting.model;


import com.chh.trustfort.accounting.enums.InvestmentSubtype;
import com.chh.trustfort.accounting.enums.InvestmentType;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "eurobond_investments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EurobondInvestment extends InvestmentVehicle {

    @Column(name = "settlement_date")
    private LocalDate settlementDate;

    @Column(name = "valuation_date")
    private LocalDate valuationDate;

    @Column(name = "maturity_date")
    private LocalDate eurobondMaturityDate;

    @Column(name = "coupon", precision = 5, scale = 2)
    private BigDecimal coupon;

    @Column(name = "yield_to_maturity", precision = 5, scale = 2)
    private BigDecimal yieldToMaturity;

    @Column(name = "time_to_call_years", precision = 5, scale = 2)
    private BigDecimal timeToCallYears;

    @Column(name = "last_coupon_payment")
    private LocalDate lastCouponPayment;

    @Column(name = "next_coupon_payment")
    private LocalDate nextCouponPayment;

    @Column(name = "clean_price", precision = 19, scale = 2)
    private BigDecimal cleanPrice;

    @Column(name = "accrued_interest", precision = 19, scale = 2)
    private BigDecimal accruedInterest;

    @Column(name = "dirty_price", precision = 19, scale = 2)
    private BigDecimal dirtyPrice;

    @Column(name = "face_value", precision = 19, scale = 2)
    private BigDecimal faceValue;

    @Column(name = "amount_payable", precision = 19, scale = 2)
    private BigDecimal amountPayable;

    @Column(name = "present_value", precision = 19, scale = 2)
    private BigDecimal presentValue;

    @Column(name = "local_value", precision = 19, scale = 2)
    private BigDecimal localValue;

    @Column(name = "exchange_rate", precision = 10, scale = 4)
    private BigDecimal exchangeRate;

    @PrePersist
    public void prePersist() {
        this.setInvestmentType(InvestmentType.FIXED_INCOME);
        this.setSubtype(InvestmentSubtype.EUROBOND);
    }
}
