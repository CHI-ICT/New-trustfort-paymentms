package com.chh.trustfort.accounting.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Data
@Builder
@Table(name = "account_categorys")
public class AccountCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 25)
    private String name;

    private int minCode = 0;
    private int maxCode = 0;

    @Column(unique = true, nullable = false, length = 50)
    private String description;
}
