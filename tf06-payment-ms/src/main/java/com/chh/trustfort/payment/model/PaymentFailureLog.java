package com.chh.trustfort.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_failure_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentFailureLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String txRef;

    private String reason;

    private BigDecimal expectedAmount;

    private BigDecimal receivedAmount;

    private String expectedCurrency;

    private String receivedCurrency;

    private String statusReturned;

    private String userPhone;

    private String gateway; // e.g., FLW, Paystack

    @CreationTimestamp
    private LocalDateTime createdAt;
}
