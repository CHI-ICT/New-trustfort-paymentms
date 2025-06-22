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
@Table(name = "cash_flow_statements")
public class CashFlowStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal operatingCashFlow;
    private BigDecimal investingCashFlow;
    private BigDecimal financingCashFlow;
    private BigDecimal netCashFlow;

    @Enumerated(EnumType.STRING)
    private StatementType type = StatementType.CASH_FLOW;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
