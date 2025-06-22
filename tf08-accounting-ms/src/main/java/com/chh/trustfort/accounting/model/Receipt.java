package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.ReceiptSource;
import com.chh.trustfort.accounting.enums.ReceiptStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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

    @Column(name = "receipt_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiptDate;

    @CreationTimestamp
    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(name = "due_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDate dueDate;


    @Enumerated(EnumType.STRING)
    private ReceiptSource source;

    @Enumerated(EnumType.STRING)
    private ReceiptStatus status;

    private String createdBy;

    @Column(name = "currency_code", nullable = false, length = 5)
    private String currencyCode;

    @Column(name = "exchange_rate", nullable = false, precision = 12, scale = 6)
    private BigDecimal exchangeRate;

    @Column(name = "base_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal baseAmount;

    @Column(name = "business_unit")
    private String businessUnit;

    @Column(name = "department")
    private String department;


}


