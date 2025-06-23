package com.chh.trustfort.payment.payload;

import com.chh.trustfort.payment.model.AppUser;
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
