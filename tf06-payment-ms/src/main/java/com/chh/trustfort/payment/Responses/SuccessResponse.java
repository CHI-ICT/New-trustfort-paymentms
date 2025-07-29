package com.chh.trustfort.payment.Responses;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class SuccessResponse {
    private String responseCode;
    private String responseMessage;
    private BigDecimal newBalance;
private String referenceCode;
    private String transactionReference;
    private String productName;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String gatewayResponse;
    private String transactionTime;

}
