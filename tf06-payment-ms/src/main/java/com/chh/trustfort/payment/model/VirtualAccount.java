package com.chh.trustfort.payment.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "virtual_accounts")
public class VirtualAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber;
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", referencedColumnName = "wallet_id")
    private Wallet wallet;

}

