package com.chh.trustfort.payment.model;

import com.chh.trustfort.payment.enums.CommissionType;
import com.chh.trustfort.payment.enums.TransactionStatus;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "commissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Commission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private Users user;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true) // <- make it nullable for now
    private Users user;


    @Column(nullable = false)
    private BigDecimal amount; // Commission amount

    @Enumerated(EnumType.STRING) // Store as String in DB
    private CommissionType commissionType; // TRANSACTION, REFERRAL, FIXED

    private String reference; // Optional transaction reference ID

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;

    // Getters and Setters
}
