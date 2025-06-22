package com.chh.trustfort.accounting.payload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AccountCategoryRequest {
    private String name;
    private int minCode;
    private int maxCode;
    private String description;
}