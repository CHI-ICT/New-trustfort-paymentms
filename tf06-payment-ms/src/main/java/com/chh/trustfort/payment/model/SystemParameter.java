package com.chh.trustfort.payment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "system_parameters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String key;

    @Column(nullable = false)
    private String value;
}
