package com.chh.trustfort.accounting.payload;

import com.chh.trustfort.accounting.model.AppUser;
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
