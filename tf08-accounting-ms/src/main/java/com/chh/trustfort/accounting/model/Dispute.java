package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.DisputeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Dispute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reference; // e.g. DSP-XXXX
    private String relatedReceiptReference;
    private String customerEmail;
    private String customerName;

    @Enumerated(EnumType.STRING)
    private DisputeStatus status;

    private String description;

    @Column(length = 500)
    private String resolution;

    private LocalDateTime raisedAt;
    private String raisedBy;

    private LocalDateTime resolvedAt;
    private String resolvedBy;
}
