package com.chh.trustfort.gateway.payload.user;

import lombok.Data;

/**
 *
 * @author dofoleta
 */
@Data
public class LoginResponsePayload {

    private String responseCode;

    private String responseMessage;

    private String idToken;

}
