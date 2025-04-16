package com.chh.trustfort.accounting.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author dofoleta
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceDTO {

    private String accountNumber;
    private BigDecimal availableBalance = BigDecimal.ZERO;
    private String alias;
    
    private String responseCode;
    private String responseMessage;

}
