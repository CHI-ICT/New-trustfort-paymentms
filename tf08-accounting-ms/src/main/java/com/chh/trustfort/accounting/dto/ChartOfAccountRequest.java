// ==== DTO: ChartOfAccountRequest.java ====
package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.enums.AccountClassification;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ChartOfAccountRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String currencyCode;

    @NotBlank
    private String subsidiaryCode;

    @NotBlank
    private String departmentCode;

    @NotNull
    private AccountClassification classification;
}