package com.chh.trustfort.accounting.Responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ApiResponse<T> {
    private String message;
    private T data;
    private boolean success;
    private String responseCode;
    private String status;
    private Object error;

    public static <T> ApiResponse<T> success(String message, T data, String responseCode, String status) {
        return new ApiResponse<>(message, data, true, responseCode, status, null);
    }

    public static <T> ApiResponse<T> failure(String message, String responseCode, String status, Object error) {
        return new ApiResponse<>(message, null, false, responseCode, status, error);
    }
}
