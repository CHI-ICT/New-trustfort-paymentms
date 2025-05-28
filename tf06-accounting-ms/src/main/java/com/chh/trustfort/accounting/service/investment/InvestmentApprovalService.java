package com.chh.trustfort.accounting.service.investment;

import com.chh.trustfort.accounting.dto.investment.InvestmentApprovalRequestDTO;

public interface InvestmentApprovalService {
    void submitForApproval(Long voucherId);
    void approve(InvestmentApprovalRequestDTO dto);
}
