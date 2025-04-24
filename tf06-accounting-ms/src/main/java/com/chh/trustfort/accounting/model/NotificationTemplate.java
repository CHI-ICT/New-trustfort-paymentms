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
@Table(name = "notification_templates")
@SuppressWarnings("PersistenceUnitPresent")
public class NotificationTemplate implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Column(name = "event_type",nullable = false, unique = false, length = 25)
    private String eventType;

    @Column(name = "medium",nullable = false, unique = false, length = 20)
    private String medium;
    
    @Column(name = "template_content",nullable = false, unique = false, length = 800)
    private String templateContent;

    @Column(name = "created_at", nullable = false, length = 30)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false, length = 30)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
}
