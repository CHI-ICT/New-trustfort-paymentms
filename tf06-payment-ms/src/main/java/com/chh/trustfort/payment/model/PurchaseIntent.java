package com.chh.trustfort.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseIntent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private BigDecimal amount;

    @Lob
    private String stringifiedData;

    private String status; // e.g., "PENDING", "PAID"

    private String txRef;  // To tie with Paystack reference

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
