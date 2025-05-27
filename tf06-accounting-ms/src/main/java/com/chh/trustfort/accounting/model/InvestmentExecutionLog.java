package com.chh.trustfort.accounting.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "investment_execution_log")
public class InvestmentExecutionLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long investmentId;
    private BigDecimal amount;
    private String executedBy;
    private LocalDateTime executedAt;
    private String remarks;
}
