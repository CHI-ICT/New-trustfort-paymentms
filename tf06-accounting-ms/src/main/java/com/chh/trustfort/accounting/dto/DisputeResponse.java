package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.enums.DisputeStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DisputeResponse {
    private String reference;
    private String relatedReceiptReference;
    private String customerEmail;
    private String customerName;
    private String description;
    private DisputeStatus status;
    private String resolution;
    private LocalDateTime raisedAt;
    private LocalDateTime resolvedAt;
}
