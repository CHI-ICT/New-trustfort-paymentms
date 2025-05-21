package com.chh.trustfort.accounting.dto;

import lombok.Data;

@Data
public class DisputeResolutionRequest {
    private String reference;
    private String resolution;
    private String resolvedBy;
}
