package com.chh.trustfort.accounting.payload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Data
public class AccountCategoryRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Let PostgreSQL generate the ID
    private Long id;

    private String name;
    private int minCode;
    private int maxCode;
    private String description;
}