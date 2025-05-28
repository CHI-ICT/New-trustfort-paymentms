package com.chh.trustfort.accounting.service.investment;

import com.chh.trustfort.accounting.dto.investment.InvestmentExecutionRequestDTO;

public interface InvestmentExecutionService {
    void executeInvestment(InvestmentExecutionRequestDTO dto);
}
