package com.chh.trustfort.accounting.payload;

import lombok.Data;

@Data
public class ApprovalActionRequest {
    private Long approvalId;
    private Long approverId;
    private boolean isApproved;
    private String comment;
}
