package com.chh.trustfort.accounting.model;


import com.chh.trustfort.accounting.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Table(name = "journal_entries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private ChartOfAccount account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType; // DEBIT or CREDIT

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "transactiondate", nullable = false)
    private LocalDate transactionDate;


    @Column
    private String description;

    @PrePersist
    public void prePersist() {
        if (transactionDate == null) {
            this.transactionDate = LocalDate.now();
        }
    }

    private String department;
    private String businessUnit;

    private String reference;

}
