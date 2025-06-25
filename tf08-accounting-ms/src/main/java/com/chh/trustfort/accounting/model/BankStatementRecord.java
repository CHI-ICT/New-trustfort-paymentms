package com.chh.trustfort.accounting.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class BankStatementRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bankReference;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private String narration;
    private String status; // e.g., MATCHED, UNMATCHED, SUSPICIOUS
    private String currency;

    private String internalReference; // link to ledger txn if matched


}
