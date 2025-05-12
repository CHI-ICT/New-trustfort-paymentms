package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.CreditNoteType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CreditNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String creditNoteNumber;

    private String invoiceNumber;

    private BigDecimal creditAmount;

    private String reason;

    private String createdBy;

    private LocalDateTime createdAt = LocalDateTime.now();}
