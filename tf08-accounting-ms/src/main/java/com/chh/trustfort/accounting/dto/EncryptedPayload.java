package com.chh.trustfort.accounting.dto;

import lombok.Data;

@Data
public class EncryptedPayload {
    private String payload; // Encrypted string (base64 or AES)
}

