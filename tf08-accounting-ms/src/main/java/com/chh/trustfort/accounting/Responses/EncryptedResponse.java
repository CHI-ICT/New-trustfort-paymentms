package com.chh.trustfort.accounting.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EncryptedResponse {
    private String encryptedPayload;
}
