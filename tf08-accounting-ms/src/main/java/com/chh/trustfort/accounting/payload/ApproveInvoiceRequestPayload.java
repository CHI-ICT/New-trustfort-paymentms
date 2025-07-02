package com.chh.trustfort.accounting.payload;

import lombok.Data;

@Data
public class ApproveInvoiceRequestPayload {
    private Long invoiceId;
    private String approverEmail;
    private String approverRole;
    private String comment;
}
