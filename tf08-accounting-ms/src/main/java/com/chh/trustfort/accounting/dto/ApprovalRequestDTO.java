package com.chh.trustfort.accounting.dto;

import lombok.Data;

@Data
public class ApprovalRequestDTO {
    private String approverEmail;
    private String role;
    private String comment;
}
