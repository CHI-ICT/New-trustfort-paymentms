package com.chh.trustfort.accounting.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.*;

import com.chh.trustfort.accounting.component.UserClass;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author DOfoleta
 */
@Data
@Entity
@Table(name = "users")
@SuppressWarnings("PersistenceUnitPresent")
public class Users implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
  
    @ManyToOne
    private UserGroup userGroup;

    @Column(name = "user_name", nullable = false, unique = true, length = 30)
    private String userName;

    @Column(name = "passcode", nullable = false, length = 225)
    private String passcode;

    @Column(name = "pin", nullable = false, length = 225)
    private String pin;

    @Column(name = "biometricData", nullable = true, length = 225)
    private String biometricData;

    @Column(name = "email_address", nullable = false, unique = true, length = 80)
    private String emailAddress;

    @Column(name = "first_name", nullable = false, length = 30)
    private String first_name;

    @Column(name = "last_name", nullable = false, length = 30)
    private String last_name;

    @Column(name = "user_type", nullable = false, length = 1)
    private String user_type="I";

    @Enumerated(EnumType.STRING)
    @Column(name = "user_class", nullable = false, length = 30)
    private UserClass userClass;


    @Column(name = "tenant_id", nullable = true, length = 15)
    private String tenantId;

//
//    @Column(name = "user_class", nullable = false, length = 30)
//    private String user_class;

    
    @Column(name = "device_id", nullable = false, length = 50)
    private String deviceId;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "created_by", nullable = false, length = 30)
    private String createdBy;

    @Column(nullable = false)
    private boolean approved;

    @Column(name = "approved_by", nullable = true, length = 30)
    private String approvedBy;

    @Column(name = "auth_sission", nullable = false)
    private boolean authenticateSession = false;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "pin_tries", nullable = false)
    private int pinTries = 0;

    @Column(name = "passcode_tries", nullable = false)
    private int passcodeTries = 0;

    @Column(name = "ecred", nullable = true, length = 120)
    private String ecred;

    @Column(name = "encryption_key", nullable = false, length = 120)
    private String encryptionKey;

    private String phoneNumber;

    private String walletId;

    @Column(name = "transaction_pin")
    private String transactionPin;

    @OneToOne(mappedBy = "users") // or "user" depending on your Wallet field name
    private Wallet wallet;

    public Wallet getWallet() {
        return wallet;
    }


}