package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.CreditNoteStatus;
import com.chh.trustfort.accounting.enums.CreditNoteType;
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
@Builder
@Entity
public class CreditNote {
//    private String creditNoteNumber;
//
//    private String invoiceNumber;
//
//    private BigDecimal creditAmount;
//
//    private String reason;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    private String payerEmail;

    private String customerName;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private CreditNoteStatus status;

    private String remarks;

    private String currency;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate issueDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_debit_note_id")
    private DebitNote linkedDebitNote;


    private String createdBy;
}
