package com.chh.trustfort.accounting.payload;

import lombok.Data;

/**
 *
 * @author dofoleta
 */
@Data
public class OmniResponsePayload {
    private String responseCode;
    private String responseMessage;
    private Object data;
}
