package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.StatementType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "equity_statements")
public class EquityStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate reportDate;

    private BigDecimal openingEquity;
    private BigDecimal retainedEarnings;
    private BigDecimal shareholderContributions;
    private BigDecimal dividends;
    private BigDecimal closingEquity;

    @Enumerated(EnumType.STRING)
    private StatementType type = StatementType.EQUITY_STATEMENT;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
