package com.chh.trustfort.payment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // You can either store the wallet's ID as a string or as a relation to Wallet
    @Column(name = "wallet_id", nullable = false)
    private String walletId;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType; // "CREDIT" or "DEBIT"

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "transaction_date", nullable = false, updatable = false)
    private Date transactionDate;
}
