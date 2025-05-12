package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.ReceivableStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Receivable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;

    private String customerName;

    private String customerAccount;

    private BigDecimal amountDue;

    private BigDecimal amountPaid = BigDecimal.ZERO;

    private String currency;

    private LocalDateTime dueDate;

    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    private ReceivableStatus status;

    private String reference;

    private LocalDateTime createdAt = LocalDateTime.now();

    private String createdBy;
}
