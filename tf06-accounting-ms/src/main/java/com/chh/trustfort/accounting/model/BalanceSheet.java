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
@Table(name = "balance_sheets")
public class BalanceSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate reportDate;

    private BigDecimal totalAssets;
    private BigDecimal totalLiabilities;
    private BigDecimal totalEquity;

    @Enumerated(EnumType.STRING)
    private StatementType type = StatementType.BALANCE_SHEET;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
