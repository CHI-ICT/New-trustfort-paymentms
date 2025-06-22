package com.chh.trustfort.payment.Responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Standardized response for error messages.
 */
@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private String responseCode;
    private String message;
}
