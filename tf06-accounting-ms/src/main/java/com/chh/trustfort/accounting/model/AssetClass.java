package com.chh.trustfort.accounting.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "asset_class")
public class AssetClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal averageReturnRate;

    private String riskLevel;

    private String regulatorCode;

    @Column(precision = 5, scale = 4)
    private BigDecimal baseInterestRate; // e.g., 0.10 = 10%

    @Column(precision = 5, scale = 4)
    private BigDecimal dividendRate;     // e.g., 0.02 = 2%

    @Column(precision = 5, scale = 2)
    private BigDecimal roiMultiplier;    // e.g., 1.50
}
