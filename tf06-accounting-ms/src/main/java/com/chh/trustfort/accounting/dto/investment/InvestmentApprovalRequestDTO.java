package com.chh.trustfort.accounting.dto.investment;

import lombok.Data;

@Data
public class InvestmentApprovalRequestDTO {
    public Long voucherId;
    public String approver;
    public String comment;
}
