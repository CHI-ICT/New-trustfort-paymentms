/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.accounting.model;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Daniel Ofoleta
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_activity")
@SuppressWarnings("PersistenceUnitPresent")
public class UserActivity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "created_by", nullable = false, length = 30)
    private String createdBy;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @ManyToOne
    private Users user;
    @Column(name = "activity", nullable = false, length = 120)
    private String activity;
    @Column(name = "description", length = 10000)
    private String description;
    @Column(name = "ip_address", nullable = false, length = 20)
    private String ipAddress;
    @Column(name = "status", nullable = false)
    private char status;
}
