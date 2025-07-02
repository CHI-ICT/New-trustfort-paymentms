package com.chh.trustfort.accounting.payload;

import lombok.Data;

@Data
public class ApproveInvoiceResponsePayload {
    private String responseCode;
    private String responseMessage;
    private Long invoiceId;
    private String status;
}
