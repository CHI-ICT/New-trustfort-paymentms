package com.chh.trustfort.payment.model;

import com.chh.trustfort.payment.enums.ReferenceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_references")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReference {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @Id
    private String txRef;  // Use tx_ref as the primary key

    @Column(name = "reference_code", nullable = false, unique = true)
    private String referenceCode;

    private String flutterwaveTxId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    private String currency;

    @Enumerated(EnumType.STRING)
    private ReferenceStatus status;

    private String gateway;  // e.g., FLW or PAYSTACK

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;




//    @Id
//    private String txRef;  // Use tx_ref as the primary key
//
//    private String userId;  // Could be phone number or user UUID
//    private BigDecimal amount;
//    private String currency;
//
//    @Enumerated(EnumType.STRING)
//    private ReferenceStatus status; // PENDING, VERIFIED, FAILED
//
//    private String gateway;  // e.g., FLW or PAYSTACK
//
//    @CreationTimestamp
//    private LocalDateTime createdAt;
//
//    private LocalDateTime verifiedAt;

}
