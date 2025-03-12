package com.chh.trustfort.gateway.payload;

import com.chh.trustfort.gateway.model.AppUser;
import lombok.Data;

/**
 *
 * @author dakinkuolie
 */
@Data
public class PlainTextPayload {
    
    private boolean error;
    private String response;
    private String plainTextPayload;
    private AppUser appUser;
}
