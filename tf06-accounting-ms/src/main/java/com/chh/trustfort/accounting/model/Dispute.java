package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.DisputeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Dispute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;

    private String issueDescription;

    private BigDecimal disputedAmount;

    @Enumerated(EnumType.STRING)
    private DisputeStatus status;

    private String raisedBy;

    private LocalDateTime raisedAt = LocalDateTime.now();

    private String resolution;

    private LocalDateTime resolvedAt;
}
