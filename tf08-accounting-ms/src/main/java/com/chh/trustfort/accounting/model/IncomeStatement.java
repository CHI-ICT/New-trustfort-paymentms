package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.StatementType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Table(name = "income_statements")
public class IncomeStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal totalRevenue;
    private BigDecimal totalExpenses;
    private BigDecimal netIncome;

    @Enumerated(EnumType.STRING)
    private StatementType type = StatementType.INCOME_STATEMENT;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
