// ==== DTO: ChartOfAccountResponse.java ====
package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.enums.AccountClassification;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChartOfAccountResponse {
    private String code;
    private String name;
    private String currencyCode;
    private String subsidiaryCode;
    private String departmentCode;
    private AccountClassification classification;
}