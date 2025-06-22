package com.chh.trustfort.payment.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class WalletBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String walletId;

    private String currency;

    private BigDecimal balance = BigDecimal.ZERO;

    // Ensure walletId + currency is unique
}
