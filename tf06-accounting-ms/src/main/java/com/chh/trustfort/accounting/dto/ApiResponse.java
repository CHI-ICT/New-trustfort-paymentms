package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.Responses.EncryptResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private String status;
    private String message;
    private Object data;

    public ApiResponse(String s) {
    }
}
