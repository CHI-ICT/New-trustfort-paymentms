package com.chh.trustfort.gateway.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author DOfoleta
 */
@Data
@Entity
@Table(name = "users")
@SuppressWarnings("PersistenceUnitPresent")
public class Users implements Serializable {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Basic(optional = false)
//    @Column(name = "id")
//    private Long id;
//
//    @ManyToOne
//    private UserGroup userGroup;
//
//    @Column(name = "user_name", nullable = false, unique = true, length = 30)
//    private String userName;
//
//    @Column(name = "passcode", nullable = false, length = 225)
//    private String passcode;
//
//     @Column(name = "pin", nullable = false, length = 225)
//    private String pin;
//
//     @Column(name = "biometricData", nullable = false, length = 225)
//    private String biometricData;
//
//    @Column(name = "email_address", nullable = false, unique = true, length = 80)
//    private String emailAddress;
//
//    @Column(name = "first_name", nullable = false, length = 30)
//    private String first_name;
//
//    @Column(name = "last_name", nullable = false, length = 30)
//    private String last_name;
//
//    @Column(name = "user_type", nullable = false, length = 1)
//    private String user_type="I";
//
//    @Column(name = "user_class", nullable = false, length = 30)
//    private String user_class;
//
//    @Column(name = "tenant_id", nullable = true, length = 15)
//    private String tenantId;
//
//    @Column(name = "device_id", nullable = false, length = 50)
//    private String deviceId;
//
//    @Column(nullable = false)
//    private boolean active;
//
//    @Column(name = "created_at", nullable = false)
//    private LocalDateTime createdAt = LocalDateTime.now();
//
//    @Column(name = "created_by", nullable = false, length = 30)
//    private String createdBy;
//
//    @Column(name = "approved_by", nullable = true, length = 30)
//    private String approvedBy;
//
//    @Column(name = "updated_at", nullable = false)
//    private LocalDateTime updatedAt = LocalDateTime.now();
//
//    @Column(name = "pin_tries", nullable = false)
//    private int pinTries = 0;
//
//    @Column(name = "passcode_tries", nullable = false)
//    private int passcodeTries = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    private UserGroup userGroup;

    @Column(name = "user_name", nullable = false, unique = true, length = 30)
    private String userName;

    @Column(name = "passcode", nullable = false, length = 225)
    private String passcode;

    @Column(name = "pin", nullable = true, length = 225)
    private String pin;

    @Column(name = "biometricData", nullable = true, length = 225)
    private String biometricData;

    @Column(name = "email_address", nullable = false, unique = true, length = 80)
    private String emailAddress;

    @Column(name = "fullname", nullable = false, length = 30)
    private String fullname;

    //    @Column(name = "last_name", nullable = false, length = 30)
//    private String last_name;
    @Column(name = "industry", nullable = true, length = 30)
    private String industry;

    @Column(name = "rc_number", nullable = true, length = 30)
    private String rc_number;

    @Column(name = "user_type", nullable = false, length = 20)
    private String user_type;

    @Column(name = "user_class", nullable = false, length = 30)
    private String user_class;

    @Column(name = "tenant_id", nullable = true, length = 15)
    private String tenantId;

    @Column(name = "device_id", nullable = true, length = 50)
    private String deviceId;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "submitted_on", nullable = false)
    @CreationTimestamp
    private LocalDateTime submittedOn;

    @Column(name = "submitted_by", nullable = false, length = 30)
    private String submittedBy;

//    @Column(nullable = false)
//    private boolean approved;

    @Column(name = "modified_by", nullable = true, length = 30)
    private String modifiedBy;

    @UpdateTimestamp
    @Column(name = "modified_on", nullable = true, length = 30)
    private LocalDateTime modifiedOn;

    @Column(name = "pin_tries", nullable = true)
    private int pinTries = 0;

    @Column(name = "passcode_tries", nullable = true)
    private int passcodeTries = 0;

}