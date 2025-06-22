package com.chh.trustfort.accounting.model;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDiscrepancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;
    private String expectedReference;
    private String actualReference;
    private BigDecimal expectedAmount;
    private BigDecimal actualAmount;

    private String issue; // e.g., MISSING_PAYMENT, AMOUNT_MISMATCH
    private LocalDateTime createdAt;
}
