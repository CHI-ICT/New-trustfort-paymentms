package com.chh.trustfort.payment.payload;

import lombok.Data;

import java.math.BigDecimal;

@Data
    public class FcmbDepositPayload {
        private String referenceCode;
        private BigDecimal amount;
    }