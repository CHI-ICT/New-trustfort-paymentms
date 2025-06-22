package com.chh.trustfort.accounting.Responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericApiResponse<T> {
    private String status;
    private String message;
    private T data;

    public static <T> GenericApiResponse<T> success(String message, T data) {
        return GenericApiResponse.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .build();
    }

    public static <T> GenericApiResponse<T> error(String message) {
        return GenericApiResponse.<T>builder()
                .status("error")
                .message(message)
                .data(null)
                .build();
    }
}
