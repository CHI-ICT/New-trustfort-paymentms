package com.chh.trustfort.payment.dto;

import lombok.Data;

@Data
public class EmailDetails {
    private String recipient;
    private String subject;
    private String body;
}