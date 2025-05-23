package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.ReceiptSource;
import com.chh.trustfort.accounting.enums.ReceiptStatus;
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
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String receiptNumber;
    private String payerName;
    private String payerEmail;
    private BigDecimal amount;
    private String currency;
    private String paymentReference;
    private LocalDateTime receiptDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private ReceiptSource source;

    @Enumerated(EnumType.STRING)
    private ReceiptStatus status;

    private LocalDateTime createdAt;
    private String createdBy;
    private String matchKey;
}


