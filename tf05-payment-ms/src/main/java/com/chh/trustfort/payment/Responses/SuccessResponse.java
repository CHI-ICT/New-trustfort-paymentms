package com.chh.trustfort.payment.Responses;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class SuccessResponse {
    private String responseCode;
    private String responseMessage;
    private BigDecimal newBalance;
    private String referenceCode;
}
