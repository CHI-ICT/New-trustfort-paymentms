// 1. ENTITY: PaymentInstallment.java
package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.InstallmentStatus;
import com.chh.trustfort.accounting.enums.InvoiceStatus;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private PayableInvoice invoice;

    private BigDecimal amount;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private InstallmentStatus status; // PENDING, PAID, etc.
}
