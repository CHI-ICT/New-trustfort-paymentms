package com.chh.trustfort.gateway.payload;

import lombok.Data;

/**
 *
 * @author dakinkuolie
 */
@Data
public class GenericPayload {
    private String response;
    private String request;
    
    private long appUserId;
}
