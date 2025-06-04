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
@Table(name = "quoted_equity_investments")
public class QuotedEquityInvestment extends InvestmentVehicle {


    @Column(name = "stock_name")
    private String nameOfStock;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "market_price", precision = 19, scale = 2)
    private BigDecimal marketPrice;

    @Column(name = "market_value", precision = 19, scale = 2)
    private BigDecimal marketValue;

    @Column(name = "dividend_received", precision = 19, scale = 2)
    private BigDecimal dividendReceived;

    @Column(name = "dividend_expected", precision = 19, scale = 2)
    private BigDecimal dividendExpected;

    @Column(name = "bonus_received")
    private String bonusReceived;

    @Column(name = "bonus_expected")
    private String bonusExpected;

    @Column(name = "disposal", precision = 19, scale = 2)
    private BigDecimal disposal;

    @Column(name = "unit_price", precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "gain_loss", precision = 19, scale = 2)
    private BigDecimal gainOrLoss;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;


    @PrePersist
    public void prePersist() {
        this.setInvestmentType(InvestmentType.CAPITAL_MARKET);
        this.setSubtype(InvestmentSubtype.QUOTED_EQUITY);
    }
}
