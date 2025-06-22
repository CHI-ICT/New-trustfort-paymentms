package com.chh.trustfort.accounting.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author DOfoleta
 */
@Data
@Entity
@Table(name = "audit_logs")
@SuppressWarnings("PersistenceUnitPresent")
public class AuditLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Column(name = "activity",nullable = false, unique = false, length = 255)
    private String activity;

    @Column(name = "performed_by",nullable = false, unique = false, length = 30)
    private String performedBy;

    @Column(name = "created_at", nullable = false, length = 30)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "details",nullable = false, unique = false, length = 800)
    private String details;
    
    @Column(name = "source_ip",nullable = false, unique = false, length = 30)
    private String sourceIp;

}
