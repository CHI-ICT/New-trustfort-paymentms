package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// EvidenceOfPayment.java
@Entity
@Data
@NoArgsConstructor
@Table(name = "evidenceofpayment", schema = "public")
@AllArgsConstructor
@Builder
public class EvidenceOfPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private PayableInvoice invoice;


    private LocalDate paymentDate;

    private BigDecimal paymentAmount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String referenceNumber;

    private LocalDateTime generatedAt;

    private String generatedBy;

    private String downloadUrl;
}