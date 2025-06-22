package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.PaymentStatus;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;
    private Long scheduleId;

    private BigDecimal amount;
    private String reference;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime attemptedAt;
}
