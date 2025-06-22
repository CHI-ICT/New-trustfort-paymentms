package com.chh.trustfort.payment.model;

import com.chh.trustfort.payment.enums.ReferenceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_references")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_code", nullable = false, unique = true)
    private String referenceCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private ReferenceStatus status;


    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "used_at")
    private LocalDateTime usedAt;


}
