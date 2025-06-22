package com.chh.trustfort.accounting.dto;// ReversalRequestDTO.java

import lombok.Data;

@Data
public class ReversalRequestDTO {
    private Long debitNoteId;
    private String reason;
    private String createdBy;
}