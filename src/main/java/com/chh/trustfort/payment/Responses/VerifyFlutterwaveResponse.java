package com.chh.trustfort.payment.Responses;

import com.netflix.discovery.shared.transport.EurekaHttpResponse;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class VerifyFlutterwaveResponse {
    private String status;
    private String message;
    private BigDecimal creditedAmount;
    private String walletId;

}
