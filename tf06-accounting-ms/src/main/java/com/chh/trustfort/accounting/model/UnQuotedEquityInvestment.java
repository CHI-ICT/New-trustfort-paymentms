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
@Table(name = "unquoted_equity_investments")
public class UnQuotedEquityInvestment extends InvestmentVehicle {

    @Column(name = "unquoted_stock")
    private String unquotedStock;

    @Column(name = "unit")
    private Integer unit;

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

}


//UnQuotedEquityInvestment