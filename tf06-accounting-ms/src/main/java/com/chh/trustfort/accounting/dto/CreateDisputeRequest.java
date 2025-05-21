package com.chh.trustfort.accounting.dto;

import lombok.Data;

@Data
public class CreateDisputeRequest {
    private String relatedReceiptReference;
    private String customerEmail;
    private String customerName;
    private String description;
    private String raisedBy;
}
