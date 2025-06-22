package com.chh.trustfort.accounting.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import com.chh.trustfort.accounting.enums.WalletStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author DOfoleta
 */

@Entity
@Table(name = "wallet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PersistenceUnitPresent")
public class Wallet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wallet_id", unique = true, nullable = false)
    private String walletId;

//    @ManyToOne
//    @JoinColumn(name = "AppUser_id", nullable = false)
//    private AppUser owner;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;


    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WalletStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedAt;

    @Column(name = "serialnumber", nullable = false)
    private Long serialNumber;

//    @Version
//    @Column(name = "version")
//    private Long version;

    @Column(name = "account_number", unique = true)
    private String accountNumber;

}
