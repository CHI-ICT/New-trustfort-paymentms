package com.chh.trustfort.accounting.model;


import com.chh.trustfort.accounting.enums.GLPostingType;
import com.chh.trustfort.accounting.enums.TransactionStatus;
import com.chh.trustfort.accounting.enums.TransactionType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountCode;  // From COA
    private String description;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private GLPostingType postingType;

    private LocalDate transactionDate;
    private String reference;
    private String businessUnit;
    private String department;

    @CreationTimestamp
    private LocalDateTime createdAt;
}


