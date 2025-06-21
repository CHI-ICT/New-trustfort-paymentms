package com.chh.trustfort.payment.model;

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
@Table(name = "global_configs")
@SuppressWarnings("PersistenceUnitPresent")
public class GlobalConfig implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Column(name = "param_key",nullable = false, unique = false, length = 80)
    private String key;

    @Column(name = "param_value",nullable = false, unique = false, length = 30)
    private String value;

    @Column(name = "created_at", nullable = false, length = 30)
    private LocalDateTime createdAt = LocalDateTime.now();
}
