package com.chh.trustfort.payment.dto;

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
public class CreditAccountDTO {

    private Long id;
    private String accountNumber;
    private String customerNumber;
    private String status;
    private String accountName;
    private String responseCode;
    private String currencyCode;
    private String restrictionCode;
    private String responseMessage;
}
