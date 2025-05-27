package com.chh.trustfort.accounting.dto.investment;

import lombok.Data;

@Data
public class InvestmentExecutionRequestDTO {
    public Long investmentId;
    public String executor;
}