package com.chh.trustfort.accounting.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.chh.trustfort.accounting.enums.PortfolioType;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "portfolio_alert")
public class PortfolioAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long investmentId;
    @Enumerated(EnumType.STRING)
    private PortfolioType type;
    private String message;
    private boolean resolved;
    private LocalDateTime createdAt;
}
