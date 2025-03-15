package com.chh.trustfort.payment.payload;

import lombok.Data;

/**
 *
 * @author dakinkuolie
 */
@Data
public class GenericPayload {
    private String response;
    private String request;
    private String idToken;
}
