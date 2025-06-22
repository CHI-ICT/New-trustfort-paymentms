package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.MatchingStatus;
import com.chh.trustfort.accounting.enums.ReceivableStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class Receivable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;

    @Column(nullable = false, unique = true)
    private String reference;

    private String customerName;

    @Column(name = "payer_email")
    private String payerEmail;


    private String customerAccount;

    private BigDecimal amountDue;

    @Column(nullable = false)
    private BigDecimal amount;

    private BigDecimal amountPaid = BigDecimal.ZERO;

    private String currency;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dueDate;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    private ReceivableStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt = LocalDateTime.now();

    private String createdBy;

    @Transient
    public BigDecimal getOutstandingAmount() {
        if (amountPaid == null) return amount;
        return amount.subtract(amountPaid).max(BigDecimal.ZERO);
    }

    @Column
    private BigDecimal matchedAmount = BigDecimal.ZERO;

    @Column
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private MatchingStatus matchingStatus = MatchingStatus.UNMATCHED;

    // In Receivable.java
    @ManyToOne
    @JoinColumn(name = "debit_note_id")
    private DebitNote debitNote;




}
